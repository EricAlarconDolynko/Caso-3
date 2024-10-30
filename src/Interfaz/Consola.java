package Interfaz;

import java.util.Scanner;

import Asimetrico.LlaveRSA;

public class Consola {

	public static void main(String[] args) {
		try (Scanner scan = new Scanner(System.in)) {
			String opcion;
			
			System.out.println("==================== Bienveido al Caso 3 =========================");
			System.out.println("A continuación escoja una opción");
			System.out.println("1. Generar Llaves Asimétricas (Opcion 1)");
			System.out.println("2. Servidor y Cliente Iterativo (Opcion 2)");
			System.out.println("3. Servidor y Cliente con delegados (Opcion 2)");
			
			opcion = scan.next();
			
			if (opcion.equals("1")) {
				LlaveRSA.generarYGuardarLlaves();
			}
			
			else if (opcion.equals("2")) {
				System.out.println("============= Tipo Cifrado ============= ");
				System.out.println("1. Simétrico");
				System.out.println("2. Asimétrico");
				
				String tipoCifrado;
				opcion = scan.next();
				if (opcion.equals("1")) {
					tipoCifrado = "Simetrico";
				}
				else {
					tipoCifrado = "Asimetrico";
				}
								
				Iterativo escenarioIterativo = new Iterativo(tipoCifrado);
				escenarioIterativo.iniciarCaso();
				
			}
			else if (opcion.equals("3")) {
				System.out.println("============= Tipo Cifrado ============= ");
				System.out.println("1. Simétrico");
				System.out.println("2. Asimétrico");
				
				String tipoCifrado;
				opcion = scan.next();
				if (opcion.equals("1")) {
					tipoCifrado = "Simetrico";
				}
				else {
					tipoCifrado = "Asimetrico";
				}
				
				System.out.println("============= Configuracion Delegados =============");
				
				System.out.println("Cuantos delegados de Clientes van a haber");
				int cantidadClientes = Integer.parseInt(scan.next());
				System.out.println("Cuantos delegados para el Servidor van a haber");
				int cantidadSockets = Integer.parseInt(scan.next());
				
				Concurrente escenarioConcurrente = new Concurrente(cantidadClientes, cantidadSockets, tipoCifrado);
				
				escenarioConcurrente.iniciarCaso();
				
			}
			else {
				System.out.println("No escogió una opción correcta tuki tuki bye bye");
			}
			
			scan.close();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}

}
