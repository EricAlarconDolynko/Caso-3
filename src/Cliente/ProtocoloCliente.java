package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import Asimetrico.*;
import Reporte.Tiempo;
import Simetrico.AutenticacionHMac;
import Simetrico.CifradoAES;
import Simetrico.DiffieHellman;

public class ProtocoloCliente{
	
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut, String idUsuario, String tipoCifrado, int numClientes, int numServidor, String modo) throws IOException {
		
		Tiempo tiempo = new Tiempo();
			
		long tiempoInicio = System.currentTimeMillis();
		boolean conexionRSA = verificarAsimetrico(pIn, pOut);
		long tiempoFin = System.currentTimeMillis();
        long deltaTiempo = tiempoFin - tiempoInicio;
        
        tiempo.guardarTiempo(modo, idUsuario, deltaTiempo, tipoCifrado, "Verificar Reto");
		
		
		if (conexionRSA = true) {
			
			tiempoInicio = System.currentTimeMillis();
			BigInteger llaveDF = verificarDiffieHellman(pIn, pOut);
			tiempoFin = System.currentTimeMillis();
			deltaTiempo = tiempoFin - tiempoInicio;
			
	        tiempo.guardarTiempo(modo, idUsuario, deltaTiempo, tipoCifrado, "Generar DiffieHellman");
			
			
			if (llaveDF != null) {
				byte[] dygestSHA = DiffieHellman.generarDigestSHA512(llaveDF);
				SecretKey llaveAES = CifradoAES.llaveCifrado(dygestSHA);
				SecretKey llaveHMac = AutenticacionHMac.llaveHMac(dygestSHA);
				
				deltaTiempo = comunicacion(pIn, pOut, llaveAES,llaveHMac, idUsuario, tipoCifrado, modo);
		        tiempo.guardarTiempo(modo, idUsuario, deltaTiempo, tipoCifrado, "Atender Consulta ");

				
			}
			else {
				System.out.println("C: Hubo Error en el protocolo DiffieHellman");
			}
			
		}
		else {
			System.out.println("C: Hubo Error en el protocolo Asímetrico");
		}
				
	}
	
	public static long comunicacion(BufferedReader pIn, PrintWriter pOut, SecretKey llaveAES, SecretKey llaveHMac,String idUsuario, String tipoCifrado ,String modo) throws IOException {
		
		int cantidad;
		
		if (modo.equals("concurrente")) {
			cantidad = 1;
		}
		else {
			cantidad = 32;
		}
		
		byte[] vectorIV = CifradoAES.generarIV();
		long tiempoPromedio = 0;
		long tiempoSuma = 0;
		
		PrivateKey llavePrivada = (PrivateKey) LlaveRSA.leerLlave("privada");


		for (int i = 1; i <= cantidad; i++) {
			
			long tiempoInicio = System.currentTimeMillis();
			
			String userIdCifrado = CifradoAES.cifrarAES(llaveAES, Integer.toString(i), vectorIV);
			pOut.println(userIdCifrado);
			
			String userHMac = AutenticacionHMac.generarHMac(llaveHMac, Integer.toString(i));
			pOut.println(userHMac);
			
			String paqueteIdCifrado = CifradoAES.cifrarAES(llaveAES, Integer.toString(i), vectorIV);
			pOut.println(paqueteIdCifrado);
			
			String paqueteHMac = AutenticacionHMac.generarHMac(llaveHMac, Integer.toString(i));
			pOut.println(paqueteHMac);
			
			
			String respuestaCifrada = pIn.readLine();
			String realRespuesta = "Error Externo";
			
			if (tipoCifrado.equals("Simetrico")) {
				realRespuesta = CifradoAES.descifrarAES(llaveAES, respuestaCifrada);
				
				
			}
			else if (tipoCifrado.equals("Asimetrico")) {
				
				try {
					realRespuesta = CifradoRSA.descifrarMensaje(llavePrivada, respuestaCifrada);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else {
				System.out.println("OCURRIO UN ERROR EN LA DIFERENCIA DE CIFRADOS!!!!!!!!!");
			}
			
			String respuestaHMacRecibir = pIn.readLine();
			boolean verifyRespuestaHMac = AutenticacionHMac.verificarHMac(llaveHMac, realRespuesta, respuestaHMacRecibir);
			
			
			if (verifyRespuestaHMac = false) {
				realRespuesta = "Error en la consulta";
			}
			
			System.out.println("C " + idUsuario +": La respuesta a mi solicitud fue " + realRespuesta);
			
			long tiempoFin = System.currentTimeMillis();
			long deltaTime = tiempoFin-tiempoInicio;
			tiempoSuma += deltaTime;

		}
		
		tiempoPromedio = tiempoSuma/cantidad;		
		pOut.println("TERMINAR");
		
		return tiempoPromedio;
		
	}
	
	public static BigInteger verificarDiffieHellman(BufferedReader pIn, PrintWriter pOut) {
		
		BigInteger llavePrivadaDF = DiffieHellman.generarClavePrivada();
		PublicKey llavePublica = (PublicKey) LlaveRSA.leerLlave("publica");
		
		try {
			BigInteger g = DiffieHellman.stringToBig(pIn.readLine());
			BigInteger p = DiffieHellman.stringToBig(pIn.readLine());
			BigInteger gx = DiffieHellman.stringToBig(pIn.readLine());
			String firmaLegible = pIn.readLine();
			byte[] firma = Base64.getDecoder().decode(firmaLegible);
			boolean verifyFirma = LlaveRSA.verificarFirma(p, g, gx, llavePublica, firma);
			if (verifyFirma = true) {
				pOut.println("OK");
				BigInteger gy = DiffieHellman.calcularExponenciacion(g, llavePrivadaDF, p);	
				pOut.println(gy);
				BigInteger llaveDF = DiffieHellman.calcularExponenciacion(gx, llavePrivadaDF, p);
				return llaveDF;
				
			}
			else {
				pOut.println("ERROR");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	public static boolean verificarAsimetrico(BufferedReader pIn, PrintWriter pOut) {
		pOut.println("SECINIT");
		PublicKey llavePublica = (PublicKey) LlaveRSA.leerLlave("publica");
		try {
			String reto = CifradoRSA.generarReto();
			String mensajeCifrado = CifradoRSA.cifrarMensaje(llavePublica, reto);
			
			pOut.println(mensajeCifrado);
			String retoRecibido = pIn.readLine();
			
			if (retoRecibido.equals(reto)) {
				pOut.println("OK");
				return true;
			}
			else {
				pOut.println("ERROR");
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
	}

}
