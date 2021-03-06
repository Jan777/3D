package ventana;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import batalla.EquipoSimple;
import batalla.PersonajeSimple;
import cliente.Cliente;
import habilidad.Habilidad;
import item.ItemLanzable;
import mensaje.MensajeActualizacionCobate;
import mensaje.MensajeBatalla;
import musica.AudioFilePlayer;
import personaje.Personaje;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;




@SuppressWarnings("serial")
public class Combate extends JFrame {

	private JPanel contentPane;
	
	private List<Point> posEquipo1 = new ArrayList<Point>();
	private List<Point> posEquipo2 = new ArrayList<Point>();
	private List <SpriteCombate> equipo1 = new ArrayList<SpriteCombate>();
	private List <SpriteCombate> equipo2 = new ArrayList<SpriteCombate>();
	String pathSprite = "src/main/resources/combate/actor/";
	String pathSounds = "src/main/resources/sound/";
	String pathCombat = "src/main/resources/combate/";

	boolean puedoAccionar;
	boolean puedoElegir;
	boolean atacarElegido;
	
	Cliente client;
	
	JPanel menuAcciones;
	
	MensajeBatalla men;
	
	private Personaje pjPropio;
	
	//botones
	private JButton btnHuir;
	private JButton btnMochila;
	private JButton btnAtacar;
	private JButton btnHabilidades;
	private JButton btnDefender;
	
	private JTextArea textArea;
	private JScrollPane mensajesScroll;
	private JList lista;
	private JScrollPane listaScroll;
	private JPanel listaPanel;
	DefaultListModel<String> listModel;
	List<String> llavesListModel;

	
	AudioFilePlayer playerMusic;

	/**
	 * Create the frame.
	 */
	public Combate(EquipoSimple equipoS1, EquipoSimple equipoS2, Cliente client) {
		
		this.client = client;
		
		playerMusic = new AudioFilePlayer (pathSounds+"battle1.ogg",70,true);
		playerMusic.start();
		this.pjPropio = client.getPj();

		
        
        
		establecerPosiciones();
		

		puedoAccionar = true;
		puedoElegir = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 745);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		

		listModel = new DefaultListModel<String>();
		llavesListModel = new ArrayList<String>();
		
		JPanel fondoBatalla = new ImagePanel(pathCombat+"carcel.jpg");		
		fondoBatalla.setBounds(0, 0, 1024, 520);
		contentPane.add(fondoBatalla);
		fondoBatalla.setLayout(null);
		
		
		int i = 0;
		for (PersonajeSimple person : equipoS1.getPersonajes()) {
			SpriteCombate player1 = new SpriteCombate(pathSprite+"actor1.png", person.getNombre(), person.getVida(), person.getEnergia(), person.getVidaAct(), person.getEnergiaAct(), true, person.getNombre().equals(pjPropio.getNombre()));
			agregarInteraccion(player1);
			player1.setLocation(posEquipo1.get(i));
			fondoBatalla.add(player1);
			equipo1.add(player1);
			i++;
		}
		i = 0;
		for (PersonajeSimple person : equipoS2.getPersonajes()) {
			SpriteCombate player1 = new SpriteCombate(pathSprite+"actor1.png", person.getNombre(), person.getVida(), person.getEnergia(), person.getVidaAct(), person.getEnergiaAct(), false, person.getNombre().equals(pjPropio.getNombre()));
			agregarInteraccion(player1);
			player1.setLocation(posEquipo2.get(i));
			fondoBatalla.add(player1);
			equipo2.add(player1);
			i++;
		}
		
		
		JPanel fondoMenu = new ImagePanel(pathCombat+"menu.jpg");		
		fondoMenu.setBounds(0, 520, 1024, 200);
		contentPane.add(fondoMenu);
		fondoMenu.setLayout(null);
		
		menuAcciones = new JPanel();
		menuAcciones.setBounds(0, 0, 256, 200);
		fondoMenu.add(menuAcciones);
		menuAcciones.setLayout(null);
		menuAcciones.setOpaque(false);
		
		btnHuir = new CombatButton("HUIR");
		btnHuir.setFocusPainted(false); 
		btnHuir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accionHuir();
			}
		});
		btnHuir.setBounds(28, 158, 200, 28);
		//menuAcciones.add(btnHuir);
		
		btnMochila = new CombatButton("MOCHILA");
		btnMochila.setFocusPainted(false); 
		btnMochila.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accionMochila();
			}
		});
		btnMochila.setBounds(28, 86, 200, 28);
		menuAcciones.add(btnMochila);
		
		btnDefender = new CombatButton("DEFENDER");
		btnDefender.setFocusPainted(false); 
		btnDefender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accionDefender();
			}
		});
		btnDefender.setBounds(28, 122, 200, 28);
		menuAcciones.add(btnDefender);
		
		btnHabilidades = new CombatButton("HABILIDADES");
		btnHabilidades.setFocusPainted(false); 
		btnHabilidades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accionHabilidad();
			}
		});
		btnHabilidades.setBounds(28, 50, 200, 28);
		menuAcciones.add(btnHabilidades);
		
		btnAtacar = new CombatButton("ATACAR");
		btnAtacar.setFocusPainted(false); 
		btnAtacar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				accionAtacar();
			}
		});
		btnAtacar.setBounds(28, 14, 200, 28);
		menuAcciones.add(btnAtacar);
		
		listaPanel = new JPanel();
		listaPanel.setBounds(256, 0, 768, 200);
		listaPanel.setOpaque(false);
		listaPanel.setLayout(null);		
		listaPanel.setVisible(false);
		fondoMenu.add(listaPanel);
		
		listaScroll = new JScrollPane();
		listaScroll.setBounds(20, 20, 728, 160);
		listaPanel.add(listaScroll);
		
		lista = new JList(listModel);
		lista.setBounds(0, 0, 728, 160);
		lista.setForeground(Color.WHITE);
		lista.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lista.setBackground(new Color(0, 120, 255));
		listaScroll.setViewportView(lista);
		
		
		mensajesScroll = new JScrollPane();
		mensajesScroll.setBounds(276, 20, 728, 160);
		mensajesScroll.setVisible(true);
		fondoMenu.add(mensajesScroll);
		
		textArea = new JTextArea();
		textArea.setBounds(0, 0, 4, 22);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textArea.setBackground(new Color(0, 120, 255));
		textArea.setEditable(false);
		mensajesScroll.setViewportView(textArea);
		
		textArea.append("Inicio el Combate\n");
		


	}

	private void agregarInteraccion(final SpriteCombate player1) {
		player1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if(puedoElegir){
					((SpriteCombate) player1).mostrarFlecha(true);
					new AudioFilePlayer(pathSounds+"sound.ogg").start();
				}
				
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				((SpriteCombate) player1).mostrarFlecha(false);
			}
			@Override
			public void mouseClicked(MouseEvent arg1) {
				if(puedoElegir && (lista.getSelectedIndex()!=-1 || atacarElegido)){
					men.setObjetivo(player1.getNombre());
					if(men.getAccion()==null)
						men.setAccion(llavesListModel.get(lista.getSelectedIndex()));
					enviarMensajeBatalla();
					//if(men.getAccion()!=null)
						
					//JOptionPane.showMessageDialog(null, men);
				}
			}
		});
	}

	private void establecerPosiciones() {
		posEquipo1.add(new Point(25,100));
		posEquipo1.add(new Point(25,240));
		posEquipo1.add(new Point(25,380));
		posEquipo1.add(new Point(150,180));
		posEquipo1.add(new Point(150,340));
		
		posEquipo2.add(new Point(879,100));
		posEquipo2.add(new Point(879,240));
		posEquipo2.add(new Point(879,380));
		posEquipo2.add(new Point(754,180));
		posEquipo2.add(new Point(754,340));
	}
	
	private void accionAtacar(){
		resetearMenu();
		atacarElegido = true;
		btnAtacar.setBackground(Color.gray);
		puedoElegir = true;
		men = new MensajeBatalla(pjPropio.getNombre(),MensajeBatalla.HABILIDAD);
		men.setAccion("atacar");
		//debo elegir al objetivo
	}
	
	private void accionHabilidad(){
		resetearMenu();
		puedoElegir = true;
		btnHabilidades.setBackground(Color.gray);
		men = new MensajeBatalla(pjPropio.getNombre(),MensajeBatalla.HABILIDAD);
		mensajesScroll.setVisible(false);
		listaPanel.setVisible(true);
		cargarHabilidades();
		//tengo que elegir la habilidad y luego el objetivo
		
	}
	
	private void accionMochila(){
		resetearMenu();
		puedoElegir = true;
		btnMochila.setBackground(Color.gray);
		men = new MensajeBatalla(pjPropio.getNombre(),MensajeBatalla.OBJETO);
		mensajesScroll.setVisible(false);
		listaPanel.setVisible(true);
		cargarMochila();
		//tengo que elegir la habilidad y luego el objetivo
	}
	
	private void accionHuir(){
		resetearMenu();
		btnHuir.setBackground(Color.gray);
		men = new MensajeBatalla(pjPropio.getNombre(),pjPropio.getNombre(),MensajeBatalla.HUIR,MensajeBatalla.HUIR);
		enviarMensajeBatalla();
		//Ya lo podria mandar
	}
	private void accionDefender(){
		resetearMenu();
		btnDefender.setBackground(Color.gray);
		men = new MensajeBatalla(pjPropio.getNombre(),pjPropio.getNombre(),MensajeBatalla.DEFENDER,MensajeBatalla.DEFENDER);
		enviarMensajeBatalla();
		//Ya lo podria mandar
	}
	
	
	
	private void resetearMenu(){
		new AudioFilePlayer(pathSounds+"sound.ogg").start();
		atacarElegido = false;
		puedoElegir = false;
		Color fondo = new Color(0,120,255);
		btnHuir.setBackground(fondo);
		btnMochila.setBackground(fondo);
		btnAtacar.setBackground(fondo);
		btnHabilidades.setBackground(fondo);
		btnDefender.setBackground(fondo);
		mensajesScroll.setVisible(true);
		listaPanel.setVisible(false);
	}
	
	private void cargarHabilidades(){
		listModel.clear();
		llavesListModel.clear();
		
		Map<String,Habilidad> hab = pjPropio.getHabilidades();
		for (String habilidad : hab.keySet()) {
			if(!habilidad.equals("atacar")){
				listModel.addElement(hab.get(habilidad).getNombre());
				llavesListModel.add(habilidad);
			}
			
		}
	}
	
	private void cargarMochila(){
		listModel.clear();
		llavesListModel.clear();
		
		Map<String,ItemLanzable> items = pjPropio.getMochilaItemLanzable();
		for (String item : items.keySet()) {
			listModel.addElement(items.get(item).getNombre());
			llavesListModel.add(item);
		}
	}
	
	private void enviarMensajeBatalla(){
		client.enviarMensajeDuranteBatalla(men);
		resetearMenu();
		ocultarMenu();
		
	}
	
	private void ocultarMenu(){
		menuAcciones.setVisible(false);
	}
	
	private void mostrarMenu(){
		menuAcciones.setVisible(true);
	}

	public void actualizarEstado(MensajeActualizacionCobate men2) {

		for (SpriteCombate player : equipo1) {
			if(player.getNombre().equals(men2.getEmisor())){
				player.actualizarEstado(men2.getVida(),men2.getEnergia());
				textArea.append(men2.getMensaje()+"\n");
				return;
			}
		}

		for (SpriteCombate player : equipo2) {
			if(player.getNombre().equals(men2.getEmisor())){
				player.actualizarEstado(men2.getVida(),men2.getEnergia());
				textArea.append(men2.getMensaje()+"\n");
				return;
			}
		}
		
	}

	public void pedirAccion() {
		mostrarMenu();	
	}

	public void cerrar() {
		playerMusic.detener();
		this.dispose();
		
	}
	

}
