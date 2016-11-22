package servidor;

import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import database.SQLiteJDBC;
import mapa.Punto;
import mensaje.*;
import personaje.FactoriaPersonaje;
import personaje.Personaje;
import raza.Humano;



public class ThreadEscuchar extends Thread{
	
	private Canal jugadores;
	private SocketCliente cliente;
	SQLiteJDBC sqcon;
	
	
	public ThreadEscuchar(Canal jugadores, SocketCliente cliente){
		this.jugadores = jugadores;
		this.cliente = cliente;
		try {
			sqcon = SQLiteJDBC.getInstance();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		
		
		intro();
		
	}
	
	private void intro(){
		try {

			MensajeAutenticacion men = cliente.pedirAutenticacion();
			
			if(men.isRegistro()){
				if(sqcon.crearUsuario(men.getUsername(), men.getPassword())){
					//si los datos son para un nuevo registro sigo con el mismo
					Personaje newper = FactoriaPersonaje.getPersonaje(men.getUsername(), men.getRaza(), men.getCasta());
					newper.setUbicacion(6, 6);
					if(sqcon.guardarPersonaje(newper))
						cliente.enviarMensajeConfirmacion(true, "");
					else
						cliente.enviarMensajeConfirmacion(false, "Error al crear el personaje, intentelo nuevamente");
				}
				else{
					//sino le digo el nombre de usuario ya existe
					cliente.enviarMensajeConfirmacion(false, "El usuario ya existe");
				}
			}
			else{
				if(sqcon.autenticarUsuario(men.getUsername(), men.getPassword())){
					
					//si el mensaje era para loguearse y cumple la autenticacion lo uno al juego
					cliente.enviarMensajeConfirmacion(true, "");
					cliente.setUsuario(men.getUsername());
					Personaje per = sqcon.getPersonaje(men.getUsername());
					if(per != null){		
						cliente.enviarMensaje(per);
						cliente.setPer(per);
						jugadores.agregarCliente(cliente, per);		
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							System.out.println("Error al entrar el personaje al mundo");
						}
						new ThreadEnviarPosicionesIniciales(jugadores, cliente).start();
						escuchar(jugadores);						
						
					}
					else{
						cliente.enviarMensajeConfirmacion(false, "Hay un error con su cuenta.");
					}
					
				}
				else{
					//le muestro un error que los datos son incorrectos
					cliente.enviarMensajeConfirmacion(false, "Nombre de usuario y contraseņa no coinciden");
				}
				
				
			}

			




		} catch (IOException e) {
			try {
				cliente.cerrar();
			} catch (IOException e1) {
				System.out.println("No se pudo cerrar el cliente");
			}
		}
		

	}
	


	private void escuchar(Canal can){
		boolean conetado = true;
		
		while(conetado){
			
			try {
				
				MensajeInteraccion mens = cliente.pedirMensajeInteraccion();
				
				if(mens.isMovimiento()){
					can.moverPersonaje(cliente.getPer(), ((MensajeMovimiento) mens).getPos());
					new ThreadEnviarInteraccion(can, mens).start();
				}
				
				if(mens.isParado()){
					can.detenerPersonaje(cliente.getPer());
					new ThreadEnviarInteraccion(can, mens).start();
				}
				
				if(mens.isCombate()){
					can.empezarCombate(cliente.getPer().getNombre(), mens.getEmisor());
					escucharCombate();
					can.terminarCombate(cliente.getPer().getNombre(), mens.getEmisor());
				}
					
				
					
				
			} catch (IOException e) {				
				can.quitarCliente(cliente);
				if(!sqcon.guardarPersonaje(cliente.getPer())){
					System.out.println("No se pudo guardar el personaje " + cliente.getUsuario());
				}
				try {
					cliente.cerrar();
				} catch (IOException e1) {
					System.out.println("No se pudo cerrar al cliente");
				}
				
				conetado = false;
				
				System.out.println("Servidor:Cliente Desconectado!");
			}
			
		}
	}

	private void escucharCombate() {
		// TODO Auto-generated method stub
		// aca tengo que empezar a escuchar las acciones del combate y actuar en funcion de eso
	}
	
	

}
