package juego;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import mapa.Punto;
import mapagrafico.dijkstra.AlgoritmoDelTacho;
import mapagrafico.dijkstra.Grafo;
import mapagrafico.dijkstra.Nodo;
import sprites.Animacion;
import sprites.Sprite;

public class TilePlayer {

	private Animacion[] animacionCaminado;
	private int xActual;
	private int yActual;
	private int xDestino;
	private int yDestino;
	protected int xIsometrica; 	// posicion real que se va dibujar
	protected int yIsometrica;
	private List<Nodo> camino;
	private int xAnterior;
	private int yAnterior;
	private Nodo paso;
	private String nombre;
	AlgoritmoDelTacho moverGordo;
	Camara camara;
	

	public TilePlayer(String nombre,String sprite, Punto point, Camara camara) {
		xDestino = xActual = xAnterior = point.getX();
		yDestino = yActual = yAnterior = point.getY();
		this.nombre = nombre;
		inicializarAnimaciones("src\\main\\resources\\personajes\\"+sprite+".png");
		this.camara = camara;
		xIsometrica = ((xActual+camara.getxActualPJ())+ - (yActual+camara.getyActualPJ()) ) * ( 128 / 2);
		yIsometrica = ((xActual+camara.getxActualPJ()) + (yActual+camara.getyActualPJ())) * ( 128 / 2);
		
	}
	public void inicializarAnimaciones(String pathPJ) {
		Sprite spriteCaminando =  new Sprite(pathPJ);
		animacionCaminado = new Animacion[8];
		for (int i = 0; i < animacionCaminado.length; i++) {
			animacionCaminado[i] = new Animacion(100, spriteCaminando.getVectorSprite(i));
		}
	}

	public void actualizarAnimaciones() {
		for (int i = 0; i < 8; i++) {
			animacionCaminado[i].actualizar();
		}
	}
	public int getxDestino() {
		return xDestino;
	}
	public int getyDestino() {
		return yDestino;
	}
	public void calcularDijkstra(Grafo grafoDeMapa, Nodo actual, Nodo destino) {
		moverGordo 	= 	new AlgoritmoDelTacho();
		//si nnunca calculas el disktstra jamas se va a mover gordo, por eso tiraba error por todos lados
		moverGordo.calcularDijkstra(grafoDeMapa, actual, destino);
		camino 		=	moverGordo.obtenerCamino(destino);
	}

	private void moverUnPaso() { // Esto tengo que ver, pero lo que hace es mover paso a paso por el camino del DI kjsoihyoas TRAMMMMMMMMMMM
		if(camino == null || camino.isEmpty() )
			return;
		paso = camino.get(0);
		xAnterior = xDestino;
		yAnterior = yDestino;
		xDestino = paso.getPunto().getX();
		yDestino = paso.getPunto().getY();
		camino.remove(0);
	}

	public void mover(Graphics2D g2d) {
		
		xActual=xDestino;
		yActual=yDestino;	
		
		int deltaX = camara.getxOffCamara()-1 + (xActual-camara.getxActualPJ());
		int deltaY = camara.getyOffCamara()-2 + (yActual-camara.getyActualPJ());

		int nx = (deltaX - deltaY ) * ( 64 / 2);
		int ny = (deltaX + deltaY) * ( 32 / 2);
/*
		if(xIsometrica < nx)
			xIsometrica+=2;

		if(xIsometrica > nx)
			xIsometrica-=2;

		if(yIsometrica < ny)
			yIsometrica++;

		if(yIsometrica > ny)
			yIsometrica--;
*/
		//xIsometrica = 100;
		//yIsometrica = 100;
		System.out.println(nx +" - " + ny);
		
		g2d.drawImage( animacionCaminado[0].getFrameActual(), nx, ny-32 , null);	
		Font fuente=new Font("Arial", Font.BOLD, 16);
		g2d.setColor(Color.RED);
		g2d.setFont(fuente);
		g2d.drawString(nombre, nx, ny - 5);
	}
	public void actualizar() {
		// no deberia ser que cunaod no estas en tu destino te tenes qu emover ganzo?
		if(xActual!=xDestino ||	yActual!=yDestino ){
			moverUnPaso();
		}		
	}

	public int getyAnterior() {
		return yAnterior;
	}
	public int getxAnterior() {
		return xAnterior;
	}

	@Override
	public String toString() {
		return this.nombre +" "+xDestino+" : "+yDestino;
	}

}
