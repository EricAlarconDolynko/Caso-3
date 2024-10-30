package Simetrico;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class DiffieHellman {

    public static final BigInteger p = new BigInteger(
        "00b49007f4f68c9c2a8a627c7c9f01559b21fa2a0f34b7c3dd620d07e2e56e2dc7976a0b3d91861ac7229b8ab6ee6a2c449ce31345629a1eda85b81705f4390129a9642e4b3643a4f8b6c2d6937ead43727bf1e5873e2749a23de4aedfcfea70fa051faa9feea1a78f571ef4ded5c117693a359212e2c6f5b6672c72bee4abb37", 16);
    public static final BigInteger g = new BigInteger("2", 10);
    
    
    
    
    
    public static BigInteger calcularExponenciacion(BigInteger g, BigInteger privateKey, BigInteger p) {
        return g.modPow(privateKey, p);
    }

    public static byte[] generarDigestSHA512(BigInteger valor){
        MessageDigest sha512;
		try {
			sha512 = MessageDigest.getInstance("SHA-512");
			return sha512.digest(valor.toByteArray());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
    }
 
    public static BigInteger generarClavePrivada() {
        SecureRandom random = new SecureRandom();
        BigInteger clavePrivada;

        do {
            clavePrivada = new BigInteger(1024, random);
        } while (clavePrivada.compareTo(p) >= 0); 
        return clavePrivada;
    }
    
    public static BigInteger stringToBig(String elemento) {
    	return new BigInteger(elemento,10);
    }
    
    
}
