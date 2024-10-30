package Asimetrico;

import javax.crypto.Cipher;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public class CifradoRSA {

    public static String cifrarMensaje(PublicKey publicKey, String mensaje) throws Exception {

    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(mensajeCifrado);
    }
    
    public static String descifrarMensaje(PrivateKey privateKey, String mensajeCifrado) throws Exception {

    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] bytesCifrados = Base64.getDecoder().decode(mensajeCifrado);

        byte[] mensajeDescifrado = cipher.doFinal(bytesCifrados);

        return new String(mensajeDescifrado, "UTF-8");
    }
    
    public static String generarReto() {
        SecureRandom random = new SecureRandom();
        BigInteger reto = new BigInteger(118, random);
        return reto.toString();
    }
}

