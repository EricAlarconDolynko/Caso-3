package Simetrico;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AutenticacionHMac {

	public static SecretKey llaveHMac(byte[] digestSHA512) {

		byte[] claveHMacBytes = new byte[32];
        System.arraycopy(digestSHA512, 32, claveHMacBytes, 0, 32);

        return new SecretKeySpec(claveHMacBytes, "HmacSHA384"); 
    }
	
	public static String generarHMac(SecretKey clave, String mensaje){
        
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA384");
			mac.init(clave);

	        byte[] hmacBytes = mac.doFinal(mensaje.getBytes("UTF-8"));

	        return Base64.getEncoder().encodeToString(hmacBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
    }
	
	public static boolean verificarHMac(SecretKey clave, String mensaje, String hmacAComparar){
        String hmacGenerado = generarHMac(clave, mensaje);
        return hmacGenerado.equals(hmacAComparar);
    }
}
