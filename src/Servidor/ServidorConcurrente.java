package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorConcurrente extends Thread{
    
    private final int puerto = 3400;
    private final int numClientes;
    private final ExecutorService pool;
    private int clientesAtendidos = 0; 
    private String tipoCifrado;
    private String modo;
    
    @Override
    public void run(){
    	try {
			ejecutarServidor();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public ServidorConcurrente(int nThreads, int numClientes, String tipoCifrado, String modo) {
        this.numClientes = numClientes;
        this.pool = Executors.newFixedThreadPool(nThreads); 
        this.tipoCifrado = tipoCifrado;
        this.modo = modo;
    }

    public void ejecutarServidor() throws IOException {
        ServerSocket ss = null;
        
        try {
            ss = new ServerSocket(puerto);
        } catch (IOException e) {
            System.err.println("No se pudo crear el socket en el puerto: " + puerto);
            System.exit(-1);
        }

        while (clientesAtendidos < numClientes) {
            try {
                Socket socket = ss.accept();
                ThreadServidor task = new ThreadServidor(socket, clientesAtendidos, tipoCifrado, modo);
                clientesAtendidos++; 
                pool.execute(task);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ss.close();
        pool.shutdown();
        System.out.println("Servidor cerrado después de atender a " + numClientes + " clientes.");
    }
}

