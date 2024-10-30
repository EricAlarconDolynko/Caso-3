package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import Asimetrico.CifradoRSA;
import Asimetrico.LlaveRSA;
import Simetrico.AutenticacionHMac;
import Simetrico.CifradoAES;
import Simetrico.DiffieHellman;
import java.util.Base64;

import javax.crypto.SecretKey;

import Almacenamiento.Bodega;

public class ProtocoloServidor {
	
	public static void procesar(BufferedReader pIn, PrintWriter pOut, String tipoCifrado, String modo) throws IOException {
		
		boolean conexionRSA = verificarAsimetrico(pIn, pOut);
		
		if (conexionRSA = true) {
			BigInteger llaveDF = verificarDiffieHellman(pIn, pOut);
			if (llaveDF != null) {
				byte[] dygestSHA = DiffieHellman.generarDigestSHA512(llaveDF);
				SecretKey llaveAES = CifradoAES.llaveCifrado(dygestSHA);
				SecretKey llaveHMac = AutenticacionHMac.llaveHMac(dygestSHA);
				comunicacion(pIn, pOut, llaveAES,llaveHMac, tipoCifrado, modo);
	
			}
			else {
				System.out.println("S: Hubo Error en el protocolo DiffieHellman");
			}

		}
		else {
			System.out.println("S: Hubo Error en el protocolo Asímetrico");
		}
		
    }
	
	public static void comunicacion(BufferedReader pIn, PrintWriter pOut, SecretKey llaveAES, SecretKey llaveHMac, String tipoCifrado, String modo) throws IOException {
		
		int cantidad;
		
		if (modo.equals("concurrente")) {
			cantidad = 1;
		}
		else {
			cantidad = 32;
		}
		byte[] vectorIV = CifradoAES.generarIV();
		PublicKey llavePublica = (PublicKey) LlaveRSA.leerLlave("publica");


		
		for (int i = 1; i <= cantidad; i++) {
			Bodega bodega = new Bodega();
			
			boolean autenticacion = true;
			
			String userIdCifrado = pIn.readLine();
			String realUserId = CifradoAES.descifrarAES(llaveAES, userIdCifrado);
			
			String userHMacRecibir = pIn.readLine();
			boolean verifyUserHMac = AutenticacionHMac.verificarHMac(llaveHMac, realUserId, userHMacRecibir);
			
			if (verifyUserHMac = false) {
				autenticacion = false;
			}
			
			String paqueteIdCifrado = pIn.readLine();
			String realPaqueteId = CifradoAES.descifrarAES(llaveAES, paqueteIdCifrado);
			
			String paqueteHMacRecibir = pIn.readLine();
			boolean verifyPaqueteHMac = AutenticacionHMac.verificarHMac(llaveHMac, realPaqueteId, paqueteHMacRecibir);
			
			if (verifyPaqueteHMac = false) {
				autenticacion = false;	
			}
			
			String respuestaBodega = bodega.obtenerEstadoPaquete(realUserId, realPaqueteId);
			if (autenticacion = false) {
				respuestaBodega = "Error en la Consulta";
			}
			
			String estadoCifrado = "Error externo";
			
			if(tipoCifrado.equals("Simetrico")) {
				estadoCifrado = CifradoAES.cifrarAES(llaveAES, respuestaBodega, vectorIV);
				pOut.println(estadoCifrado);
				
			}
			else if (tipoCifrado.equals("Asimetrico")) {
				try {
					estadoCifrado = CifradoRSA.cifrarMensaje(llavePublica, respuestaBodega);
					pOut.println(estadoCifrado);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			else {
				System.out.println("OCURRIO UN ERROR EN LA DIFERENCIA DE CIFRADOS!!!!!!!!!");
			}
			
			String estadoHMac = AutenticacionHMac.generarHMac(llaveHMac, estadoCifrado);
			pOut.println(estadoHMac);
			
			
		}
		String terminacion = pIn.readLine();
		if (terminacion.equals("TERMINAR")) {
			System.out.println("S: Terminación de conexión correcta");
		}
		else {
			System.out.println("S: Error en Terminación");
		}
		
		
	}

	public static BigInteger verificarDiffieHellman(BufferedReader pIn, PrintWriter pOut) {
		
		BigInteger llavePrivadaDF = DiffieHellman.generarClavePrivada();
		PrivateKey llavePrivada = (PrivateKey) LlaveRSA.leerLlave("privada");
		
		BigInteger p = DiffieHellman.p;
		BigInteger g = DiffieHellman.g;
		BigInteger gx = DiffieHellman.calcularExponenciacion(g, llavePrivadaDF, p);
		
		pOut.println(g);
		pOut.println(p);
		pOut.println(gx);
		byte[] firma = LlaveRSA.firmarValores(p, g, gx, llavePrivada);
		String firmaLegible = Base64.getEncoder().encodeToString(firma);
		pOut.println(firmaLegible);
		try {
			String verifyFirma = pIn.readLine();
			if (verifyFirma.equals("OK")) {
				BigInteger gy = DiffieHellman.stringToBig(pIn.readLine());
				BigInteger llaveDF = DiffieHellman.calcularExponenciacion(gy, llavePrivadaDF, p);
				return llaveDF;

			}
			else {
				System.out.println("S: Error en la verificación Firma");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
    public static boolean verificarAsimetrico(BufferedReader pIn, PrintWriter pOut) throws IOException {
    	
		PublicKey llavePublica = (PublicKey) LlaveRSA.leerLlave("publica");
		PrivateKey llavePrivada = (PrivateKey) LlaveRSA.leerLlave("privada");
		
    	if (pIn.readLine().equals("SECINIT")) {
 	        String cifradoReto = pIn.readLine();
 	        try {
				String decifrarReto = CifradoRSA.descifrarMensaje(llavePrivada, cifradoReto);
				pOut.println(decifrarReto);
				String verificarOK = pIn.readLine();
				
				if (verificarOK.equals("OK")) {
					return true;
				}
				else {
					return false;
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
 	        
	    }
    	else {
    		System.out.println("S: No se pudo Iniciar Conexión");
    	}
    	return false;
	} 
	
}
