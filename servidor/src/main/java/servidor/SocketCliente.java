package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import mensaje.*;




public class SocketCliente {
	
	String usuario;
	Socket cliente;
	
	public SocketCliente(Socket cliente){
		this.cliente = cliente;
	}
	
	public void setUsuario(String usuario){
		this.usuario = usuario;
	}
	
	public void enviarMensaje(Object men) throws IOException{
		DataOutputStream salida = new DataOutputStream(
				cliente.getOutputStream());
		final Gson gson = new Gson();		
		salida.writeUTF(gson.toJson(men));
		
	}
	
	public void enviarMensajeServidor(String mensaje) throws IOException{
		this.enviarMensaje(new Mensaje("Servidor", mensaje));
	}
	
	public void cerrar() throws IOException{
		cliente.close();
	}
	
	public String getUsuario(){
		return usuario;
	}
	
	public Mensaje pedirMensaje() throws IOException{
		DataInputStream lectura = new DataInputStream(
				cliente.getInputStream());
		String leido = lectura.readUTF();
		Gson gson = new Gson();
		Mensaje men = gson.fromJson(leido, Mensaje.class);
		return men;
	}
	
	
	public MensajeAutenticacion pedirAutenticacion() throws IOException{
		DataInputStream lectura = new DataInputStream(
				cliente.getInputStream());
		String leido = lectura.readUTF();
		Gson gson = new Gson();
		MensajeAutenticacion men = gson.fromJson(leido, MensajeAutenticacion.class);
		return men;
	}
	
	public void enviarMensajeConfirmacion(boolean estado,String mensaje) throws IOException{
		this.enviarMensaje(new MensajeConfirmacion(estado, mensaje));
	}
	
	

}