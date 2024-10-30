package Interfaz;

import Cliente.Cliente;
import Servidor.ServidorIterativo;

public class Iterativo {
	
	Cliente cliente;
	ServidorIterativo servidor;

	public Iterativo(String tipoCifrado) {
		this.cliente = new Cliente("iterativo", tipoCifrado, 1, 1, "iterativo");
		this.servidor = new ServidorIterativo(tipoCifrado, "iterativo");
	}
	
	public void iniciarCaso() {

		long tiempoInicio = System.currentTimeMillis();

        servidor.start();
        cliente.start();

        try {
            servidor.join();
            cliente.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long tiempoFin = System.currentTimeMillis();
        long deltaTiempo = tiempoFin - tiempoInicio;
        
        System.out.println("==================== TIEMPO TOTAL ITERATIVO ==================== ");
        System.out.println("Tiempo total de ejecución: " + deltaTiempo + " ms");
    }
		
}
