package Interfaz;

import java.util.ArrayList;

import Cliente.Cliente;
import Servidor.ServidorConcurrente;

public class Concurrente {

	private int delegadosCliente;
	private int delegadosServidor;
	private ServidorConcurrente servidor;
	private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
	private String tipoCifrado;
	
	public Concurrente(int delegadosCliente, int delegadosServidor, String tipoCifrado) {
		this.delegadosCliente = delegadosCliente;
		this.servidor = new ServidorConcurrente(delegadosServidor,delegadosCliente, tipoCifrado, "concurrente");
		this.tipoCifrado = tipoCifrado;
		this.delegadosServidor = delegadosServidor;
	}
	
	public void iniciarCaso() {
		long tiempoInicio = System.currentTimeMillis();

		try {
						
			servidor.start();
			for (int i = 1; i <= delegadosCliente; i++) {
				Cliente cliente = new Cliente(Integer.toString(i), tipoCifrado, delegadosCliente, delegadosServidor, "concurrente");
				clientes.add(cliente);
				cliente.start();
			}
			
			for (Cliente clienteActual : clientes) {
				clienteActual.join();				
			}
						
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long tiempoFin = System.currentTimeMillis();
        long deltaTiempo = tiempoFin - tiempoInicio;
        
        System.out.println("==================== TIEMPO TOTAL CONCURRENTE ==================== ");
        System.out.println("Tiempo total de ejecución: " + deltaTiempo + " ms");
		
		
	}
}
