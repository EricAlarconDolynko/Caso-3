package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread{
	
	public static final int PUERTO = 3400;
    public static final String SERVIDOR = "localhost";
    private String id;
    private String tipoCifrado;
    private int numClientes;
    private int numServidores;
    private String modo;
    
    public Cliente(String id, String tipoCifrado, int numClientes, int numServidores, String modo) {
    	this.id = id;
    	this.tipoCifrado = tipoCifrado;
    	this.numClientes = numClientes;
    	this.numServidores = numServidores;
    	this.modo = modo;
    }
    
    @Override 
    public void run() {
    	ejecutarCliente();
    }

    public void ejecutarCliente(){
    	
        Socket socket = null;
        PrintWriter escritor = null;
        BufferedReader lector = null;

        try {
            socket = new Socket(SERVIDOR, PUERTO);

            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
        	
			ProtocoloCliente.procesar(stdIn, lector, escritor, id, tipoCifrado, numClientes, numServidores, modo);
			stdIn.close();
		    escritor.close();
		    lector.close();
		    socket.close();
		     
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
}
