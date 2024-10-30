package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorIterativo extends Thread{
	
	public static final int PUERTO = 3400;
	private String tipoCifrado;
	private String modo;
	
	public ServidorIterativo(String tipoCifrado, String modo) {
		this.tipoCifrado = tipoCifrado;
		this.modo = modo;
	}
	
	@Override
	public void run() {
		ejecutarServidor();
	}

	public void ejecutarServidor(){
	    ServerSocket ss = null;
	    boolean continuar = true;

	    try {
	        ss = new ServerSocket(PUERTO);
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(-1);
	    }

	    while (continuar) {
	        try {
	        	Socket socket = ss.accept();
	            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
	            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	            ProtocoloServidor.procesar(lector, escritor, tipoCifrado, modo);
	            escritor.close();
	            lector.close();
	            socket.close();
	            continuar = false;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	}
	
}
