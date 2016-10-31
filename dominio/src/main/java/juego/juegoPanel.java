package juego;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import grafico.Sprite;
import mapa.Mapa;


 // No vamos a tener ID es un solo juego :v.
@SuppressWarnings("serial")
public class juegoPanel extends Component implements Runnable, KeyListener{
	
	private static final int ANCHO = 800;
	private static final int ALTO = 600;
	private Thread thread;
	private boolean ejecutando;
	private Graphics2D g;
	private Mapa mapa;

	public juegoPanel() {
		setPreferredSize(new Dimension(ANCHO, ALTO));
		setFocusable(true);
		requestFocus();
		//addKeyListener(this);
		thread = new Thread(this);
		thread.start();
	}
	

	/**
	 * Aca es donde se actualiza el contenido del juego y se dibuja.	
	 */
	@Override
	public void run(){
		
		
		while(ejecutando) {

			actualizar();				//Actualizo el juego
			hacerDibujos();				//Actualizo el dibujo
			dibujar();					//Dibujo en la patanlla.

			try {
				Thread.sleep(200); //Hacer calculos para sacar el tiempo para que de 60FPS(o 30)
			}
			catch(Exception e) {
				e.printStackTrace();
			}

		}
	}
	/*
	 * si logro sincronizar esto, ya queda pipi cucu 
	 */
	
	private void actualizar() {
	mapa.actualizar();
	}
	private void dibujar() {
	}
	private void hacerDibujos() {
	}
	
	/**
	 * Ver bien esto para hacerlo mejor:
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

/*
	@SuppressWarnings("unused")
	private void paint(Graphics2D g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		Sprite logo = new Sprite("src\\main\\resources\\logo.png");
		logo.putSprite(g2d, 300, 0);
		
	}
*/
	

}