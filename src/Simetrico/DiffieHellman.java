package Simetrico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiffieHellman {

    public static BigInteger p = new BigInteger(
        "00b49007f4f68c9c2a8a627c7c9f01559b21fa2a0f34b7c3dd620d07e2e56e2dc7976a0b3d91861ac7229b8ab6ee6a2c449ce31345629a1eda85b81705f4390129a9642e4b3643a4f8b6c2d6937ead43727bf1e5873e2749a23de4aedfcfea70fa051faa9feea1a78f571ef4ded5c117693a359212e2c6f5b6672c72bee4abb37", 16);
    public static  BigInteger g = new BigInteger("2", 10);
    
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
    
    public static BigInteger[] parsearPyG(StringBuilder inputString) {
    	
    	String input = inputString.toString();
        Pattern primePattern = Pattern.compile("prime:\\s+([0-9a-fA-F:\\s]+)", Pattern.MULTILINE);
        Pattern generatorPattern = Pattern.compile("generator:\\s+(\\d+)", Pattern.MULTILINE);

        Matcher primeMatcher = primePattern.matcher(input);
        Matcher generatorMatcher = generatorPattern.matcher(input);

        StringBuilder primeHex = new StringBuilder();
        BigInteger p = null;
        BigInteger g = null;

        if (primeMatcher.find()) {
            String primeText = primeMatcher.group(1).replaceAll("[:\\s]", "");
            p = new BigInteger(primeText, 16); 
        }

        if (generatorMatcher.find()) {
            String generatorText = generatorMatcher.group(1);
            g = new BigInteger(generatorText); 
        }

        if (p == null || g == null) {
            throw new RuntimeException("No se pudieron extraer los valores de prime y generator.");
        }

        return new BigInteger[]{p, g};
    }
    
    public static BigInteger[] generarPyG() throws Exception {
    	
		Process process = Runtime.getRuntime().exec("OpenSSL-1.1.1h_win32\\openssl dhparam -text 1024");
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		StringBuilder output = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			output.append(line).append("\n");
		}
		
		reader.close();
		process.waitFor();
		
		BigInteger[] respuesta = parsearPyG(output);
		
		return respuesta;
		
	} 
    
}
