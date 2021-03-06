package juego;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cliente.Cliente;
import cliente.EnviadorPosicion;
import item.ItemEquipo;
import mapa.Punto;
import mapagrafico.MapaGrafico;
import mapagrafico.TileCofre;
import musica.AudioFilePlayer;
import personaje.Personaje;
import tiles.TilePersonajeLocal;
import tiles.TilePersonajeRemoto;
import ventana.CombatButton;


@SuppressWarnings("serial")
public class JuegoPanel extends Component implements Runnable{

	public static final int ANCHO = 800;
	public static final int ALTO = 600;
	public static final int fps = 60;
	public static double timePerTick = 1000000000/fps;
	
	protected JFrame padre;
	protected Opciones opciones;
	protected Cliente cliente;
	protected EnviadorPosicion env;
	protected ControlPanel controlPanel;
	private MapaGrafico mapa;
	private Thread thread;
	private Mouse mouse;
	private double delta = 0;
	private boolean ejecutando = true;
	private TilePersonajeLocal pjDibujo;
	private Camara camara;
	private HashMap<String, TilePersonajeRemoto> personajes;
	private HashMap<String, TileCofre > itemEquipo;
	AudioFilePlayer playerMusic;
	private boolean jugar = true;

	public JuegoPanel(JFrame padre,Punto spaw, Personaje pj,String nombreMapa, Cliente cliente) {
		this.padre = padre;
		this.cliente = cliente;
		this.personajes = new HashMap<String, TilePersonajeRemoto>();
		this.itemEquipo = new HashMap<String, TileCofre>();
		env = new EnviadorPosicion(cliente, pj.getNombre(),nombreMapa, pj.getSprite());
		setPreferredSize(new Dimension(ANCHO, ALTO));
		setFocusable(true);
		requestFocus();
		opciones = new Opciones(pj);
		mouse  = new Mouse();
		camara = new Camara(ANCHO, ALTO);
		addMouseListener(mouse);
		pjDibujo = new TilePersonajeLocal(spaw,pj,mouse,camara);  
		mapa 	 = new MapaGrafico(nombreMapa,pjDibujo,camara, env,personajes,itemEquipo);
	
		playerMusic = new AudioFilePlayer ("src/main/resources/sound/mapsong2.ogg",80,true);
		playerMusic.start();
		
		thread 	 = new Thread(this);
		thread.start();
	}


	@Override
	public void run(){

		long now;
		long lastTime = System.nanoTime();
		long primeravez = System.nanoTime();


		while(ejecutando) {

			now = System.nanoTime();
			delta += (now - lastTime)/timePerTick;
			lastTime = now;
			if(delta >=1){  

				actualizar();
				repaint();
				delta--;
			}
		}
	}

	public void actualizar() {
		mouse.actualizar();
		pjDibujo.actualizar();
		mapa.actualizar();
		mouseInteracion();
	}


	private void mouseInteracion() {
		if(mouse.isInteraccion()){
			
			String alguien = hayAlguien(mouse.getPosInt());
			if(alguien != null)
				cliente.enviarMensajeCombate(alguien);
		
			String itemE = hayAlgo(mouse.getPosInt());

			if(itemE != null){
				itemEquipo.get(itemE).abrir();
				cliente.pedirItem(itemE);
			}
				
			mouse.setInteraccion(false);
		}
		if(mouse.isMenu()){
			opciones.setVisible(true);
			mouse.setMenu(false);
		}
	}

	private String hayAlguien(Punto posInt) {
		int deltaX = posInt.getX() - camara.getxOffCamara() + camara.getxActualPJ();
		int deltaY = posInt.getY() - camara.getyOffCamara() + camara.getyActualPJ();


		for (String persona : personajes.keySet()) {
			int x = personajes.get(persona).getXDestino();
			int y = personajes.get(persona).getYDestino();
			if(x==deltaX	 && y == deltaY)
				return persona;				
					
		}
		return null;
	}
	private String hayAlgo(Punto posInt) {
		int deltaX = posInt.getX() - camara.getxOffCamara() + camara.getxActualPJ();
		int deltaY = posInt.getY() - camara.getyOffCamara() + camara.getyActualPJ();
		
		
		for (String itemE : itemEquipo.keySet()) {
			int x = itemEquipo.get(itemE).getX();
			int y = itemEquipo.get(itemE).getY();
			if(x==deltaX	 && y == deltaY){
				mapa.cambiarSprite(x,y,5);
				return itemE;							
			}
		}
		return null;
	}


	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		if(jugar){
			mapa.dibujar(g2d);
			jugar = false;
		}
		mapa.mover(g2d);
	}

	public void nuevoMovimientoPersonajes(String pj, String sprite, Punto point){
		TilePersonajeRemoto player = personajes.get(pj);
		if (player == null){
			
			player= new TilePersonajeRemoto(pj,sprite,point, camara);
			personajes.put(pj, player );
			repaint();
			
		}
		else{
			mapa.moverPlayer(player,point);
		}
	}

	public void nuevaDetencionPersonaje(String pj){ //estp creo que vuela.
		// aca te envio que el personaje llego a su destino, 
		// si por las dudas no llego todavia moverlo magicamente.
	}


	public void detener() {
		ejecutando = false;
		playerMusic.detener();
		
	}


	public void quitarPersonaje(String emisor) {
		personajes.remove(emisor);
		
	}

}
