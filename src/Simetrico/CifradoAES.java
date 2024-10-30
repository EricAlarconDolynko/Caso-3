package Simetrico;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CifradoAES {

	public static SecretKey llaveCifrado(byte[] digestSHA512) {

		byte[] claveCifradoBytes = new byte[32];
        System.arraycopy(digestSHA512, 0, claveCifradoBytes, 0, 32);

        return new SecretKeySpec(claveCifradoBytes, "AES"); 
    }
	
	
	public static byte[] generarIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
	
	public static String cifrarAES(SecretKey llaveAES, String mensaje, byte[] iv){
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, llaveAES, ivSpec);

	        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));

	        String mensajeCifradoBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);

	        return Base64.getEncoder().encodeToString(iv) + ":" + mensajeCifradoBase64;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
        
    }
	
	public static String descifrarAES(SecretKey llaveAES, String mensajeCifrado){

		String[] partes = mensajeCifrado.split(":");
        if (partes.length != 2) {
            throw new IllegalArgumentException("El formato del mensaje cifrado no es válido.");
        }
        byte[] iv = Base64.getDecoder().decode(partes[0]);
        byte[] mensajeCifradoBytes = Base64.getDecoder().decode(partes[1]);

        Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, llaveAES, new IvParameterSpec(iv));

	        byte[] mensajeDescifradoBytes = cipher.doFinal(mensajeCifradoBytes);

	        return new String(mensajeDescifradoBytes, "UTF-8");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
        
    }
}
