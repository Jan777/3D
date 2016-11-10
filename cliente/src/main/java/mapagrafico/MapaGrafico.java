package mapagrafico;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.ImageIcon;
import juego.Camara;
import juego.JuegoPanel;
import mapagrafico.dijkstra.Grafo;
import mapagrafico.dijkstra.MatrizBoolean;
import mapagrafico.dijkstra.Nodo;
import mensaje.MensajeMovimiento;
import personaje.Personaje;
import sprites.CargaImagen;
import sprites.Sprite;


public class MapaGrafico {

	protected int id;
	protected int alto;
	protected int ancho;
	protected String nombre;


	// BUGERO
	protected int x;
	protected int y;

	protected boolean enMovimiento;


	protected String sprites;
	private static Image[] spriteMapa;	
	protected Tile[][] tiles;
	protected Tile[][]  tilesObstaculo; 
	protected boolean[][] obstaculos; 
	protected TilePersonaje pj; // cliente
	protected Map<String,Personaje> personajes; // esto server

	protected Camara cam;

	protected int xDestino;
	protected int yDestino;
	protected int xActual;
	protected int yActual;

	protected Grafo grafoDeObstaculo;
	protected List<Nodo> camino;
	private Nodo nodoActual;

	public MapaGrafico(String nombre,TilePersonaje pj) {
		File path = new File("src\\main\\resources\\mapas\\"+nombre+".txt");
		this.pj = pj;
		enMovimiento = false;
		xDestino = pj.getXDestino();
		yDestino = pj.getYDestino();

		xActual = -xDestino;
		yActual = -yDestino;

		this.nombre = nombre;
		spriteMapa = new Image[7];

		Scanner sc = null;
		try {
			sc = new Scanner(path);
		} catch (FileNotFoundException e) {
			// no se encutra el mapa -nombre-
			e.printStackTrace();
		}
		this.id = sc.nextInt();
		this.ancho=sc.nextInt();
		this.alto=sc.nextInt();
		this.sprites=sc.next();
		cargarSprite();

		this.tiles = new Tile[ancho][alto];
		this.tilesObstaculo  = new Tile[ancho][alto];
		this.obstaculos = new boolean[ancho][alto];
		/**
		 * no hace falta pero para que se entienda
		 */
		int sprite;
		for (int i = 0; i < ancho ; i++) {
			for (int j = 0; j < alto; j++) {
				sprite = sc.nextInt();
				tiles[i][j] = new Tile(i,j,sprite);
			}
		}
		int obstaculo;
		for (int i = 0; i < ancho ; i++) {
			for (int j = 0; j < alto; j++) {
				obstaculo = sc.nextInt();
				obstaculos[i][j] = obstaculo>=1?true:false;
			}
		}

		grafoDeObstaculo = new Grafo(new MatrizBoolean(obstaculos, ancho, alto));
		camino = new ArrayList<Nodo>();



		sc.close();
		this.personajes=new HashMap<String,Personaje>();
	}


	private void cargarSprite() {
		if ( sprites.equals("exterior") ) 
			load("exterior");
		else if( sprites.equals("test"))
			load("test");
		else
			System.exit(1);
	}

	public boolean EnMovimiento() {
		return enMovimiento;
	}

	/**
	 * cambiar por hoja:
	 * @param nombre
	 */
	private void load(String nombre) {
		String relativo = "src\\main\\resources\\mapas\\";
		Sprite.inicializar(relativo+nombre+"\\piso.png",relativo+nombre+"\\pj.png");
		
		
		spriteMapa[0] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\00.png");
		spriteMapa[1] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\01.png");
		spriteMapa[2] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\02.png");
		spriteMapa[3] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\03.png");
		spriteMapa[4] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\04.png");
		spriteMapa[5] = loadImage("src\\main\\resources\\mapas\\"+nombre+"\\05.png");
		spriteMapa[6] = loadImage("src\\main\\resources\\mapas\\99.png");
		

	}

	public static Image loadImage(String path) {
		ImageIcon i = new ImageIcon(path);
		return i.getImage();
	}
	public static Image getImage(int sprite) {
		return spriteMapa[sprite];
	}

	public boolean posicionValida(int x, int y){
		return dentroDelMapa(-pj.getXDestino(),-pj.getYDestino()) && ! hayObstaculo(-pj.getXDestino(),-pj.getYDestino());


	}

	public boolean hayObstaculo(int x,int y){
		return obstaculos[x][y];
	}

	private boolean dentroDelMapa(int x, int y) {
		return x>=0 && y>=0 && x<alto && y<ancho;
	}


	public void agregarPersonaje(Personaje pj){
		personajes.put(pj.getNombre(), pj);
	}

	public boolean recibirMensajeMovmiento(MensajeMovimiento men){
		Personaje aMover = personajes.get(men.getEmisor());

		if(aMover.isPuedoMoverme()){
			aMover.setUbicacion(men.getPos());
			return true;
		}
		return false;
	}

	public Personaje getPersonaje(String per) {
		return personajes.get(per);
	}

	public void actualizar() {

		if( pj.getNuevoRecorrido() && posicionValida(-pj.getXDestino(),-pj.getYDestino()) )	{
			pj.mover();
			
			camino = grafoDeObstaculo.getCamino(xActual,yActual,-pj.getXDestino(),-pj.getYDestino());
		}
		if( !enMovimiento && ! camino.isEmpty())
			moverUnPaso();
	}

	private void moverUnPaso() {
		System.out.println(camino);
		nodoActual = camino.get(0);
		xDestino = -nodoActual.getPunto().getX();
		yDestino = -nodoActual.getPunto().getY();
		camino.remove(0);
		xActual = -xDestino;
		yActual = -yDestino;
	}


	/**
	 * tengo que buscar la forma de dibujar solo la pantalla.
	 *
	 * 			      (0,0)
	 * 			 (0,1)(1,1)(1,0)
	 *		(0,2)(1,2)(2,2)(2,1)(2,0)
	 */
	public void dibujar(Graphics2D g2d) {
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, 810, 610);		
		for (int i = 0; i <  alto; i++) { 
			for (int j = 0; j < ancho ; j++) { 
				tiles[i][j].dibujar(g2d,xDestino+JuegoPanel.xOffCamara,yDestino+JuegoPanel.yOffCamara);			
			}
		}
	}

	public void mover(Graphics2D g2d) {
		g2d.setBackground(Color.BLACK);
		g2d.clearRect(0, 0, 810, 610);		
		
		//Tiene que ser uno por uno entonces si cancelo termino el movimiento (sino se descuajaina todo).
		x = tiles[0][0].getXIso(); // puedo agarrar el centro. pero por ahora asi.
		y = tiles[0][0].getYIso();
		
		for (int i = 0; i <  alto; i++) { 
			for (int j = 0; j < ancho ; j++) { 
				tiles[i][j].mover(g2d,xDestino+ JuegoPanel.xOffCamara,yDestino+JuegoPanel.yOffCamara);
			}
		}
		g2d.drawImage( getImage(6), 0, 0 , null);	
		termino();
	}

	private void termino() {
		if ( x == tiles[0][0].getXIso() && y == tiles[0][0].getYIso() )
			enMovimiento = false;
		else 
			enMovimiento = true;
	}
}
