package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadServidor extends Thread {
    private Socket sktCliente = null;
    private int id; 
    private String tipoCifrado;
    private String modo;

    public ThreadServidor(Socket pSocket, int pId, String tipoCifrado, String modo) {
        this.sktCliente = pSocket;
        this.id = pId;
        this.tipoCifrado = tipoCifrado;
        this.modo = modo;
    }

    public void run() {

        try {
            PrintWriter escritor = new PrintWriter(sktCliente.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(sktCliente.getInputStream()));

            ProtocoloServidor.procesar(lector, escritor, tipoCifrado, modo);

            escritor.close();
            lector.close();
            sktCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
