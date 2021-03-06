package servidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import database.SQLiteJDBC;

public class Servidor extends Thread{

	public static String mensaje;
	private int puerto;
	private Canal jugadores;
	private JFrame ventanaServidor;
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	private JButton btnEnviar;
	private String sTexto;
	
	public Servidor() throws IOException, ClassNotFoundException, SQLException{
		SQLiteJDBC sqcon = SQLiteJDBC.getInstance();
		ItemsEquipo itemsEquipables = ItemsEquipo.getInstance();
		loadProperty("server.properties");
		
		ventanaServidor = new JFrame();
		ventanaServidor.setTitle("Servidor de Loro of ring's");
		ventanaServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventanaServidor.setBounds(100, 100, 500, 400);
		ventanaServidor.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		ventanaServidor.setContentPane(contentPane);
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		textField = new JTextField();

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) 
					agregarTexto();				
			}
		});

		panel_1.add(textField);
		textField.setColumns(30);
		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				agregarTexto();
			}

		});
		panel_1.add(btnEnviar);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.GREEN);
		
		jugadores = new Canal("General", 200,200,this.textArea,sqcon);
	}


	private void agregarTexto() {
		sTexto = textField.getText();
		if(!sTexto.equals("")){
			textArea.append(sTexto+"\n");
			textField.setText("");
		}
	}



	@Override
	public void run(){

		try{
			ServerSocket server = new ServerSocket(puerto);
			SocketCliente cliente;
			this.textArea.append("¡Arranco el servidor!\n");
			while(true){
				cliente = new SocketCliente(server.accept());
				//cliente.enviarMensajeServidor("Conectado!");
				this.textArea.append("Cliente Conectado.\n");
				//clientes.add(cliente);
				new ThreadEscuchar(jugadores,cliente,this.textArea).start();
				cliente = null;
			}

		}
		catch(IOException e){
			this.textArea.append("No arranco el servidor. ERROR FATAL.\nDetalle:\n"+
					e.toString()+"\n");
		}		
	}

	private void loadProperty(String dir) throws IOException{
		Properties propiedades = new Properties();
		InputStream entrada = null;	
		entrada = new FileInputStream(dir);
		propiedades.load(entrada);

		String puertoString = propiedades.getProperty("port");
		puerto =  Integer.parseInt(puertoString);
	}

	public static void main(String[] args) {
		Servidor escuchar = null;
		try {
			escuchar = new Servidor();
			escuchar.ventanaServidor.setVisible(true);
			escuchar.start();
			//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			escuchar.sTexto = "";
			boolean conectado = true;
			while(! escuchar.compararEntrada("FIN") && conectado) {		  
				//	 No entiendo si pongo una salida de pantalla funciona el comando sino no :( 
				//aca hago comandos locos
				if( escuchar.sTexto.equals("kick") ){
					escuchar.textArea.append("comer a marta.\nmatar a lucas.");
					escuchar.sTexto = "";
				}
			}
			escuchar.textArea.append("Adios!");
			System.exit(0);

		} catch (IOException | ClassNotFoundException | SQLException e) {
			escuchar.textArea.append("Error: "+e.toString()+"\n");
		}
	}

	private boolean compararEntrada(String string) {
		return sTexto.equals(string);
	}

}
