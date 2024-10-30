package Asimetrico;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.NoSuchAlgorithmException;


public class LlaveRSA {
	
	public static Key[] generarLlaves() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keyPair = keyGen.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            return new Key[] { privateKey, publicKey };

        } catch (NoSuchAlgorithmException e) {
            System.out.println("El algoritmo RSA no está disponible.");
            e.printStackTrace();
            return null;
        }
    }
	
	public static void generarYGuardarLlaves() {
        Key[] llaves = generarLlaves();
        if (llaves == null) {
            System.out.println("No se pudieron generar las llaves.");
            return;
        }

        String carpeta = "llaves";
        File directorio = new File(carpeta);
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        try {
            String publicKeyPath = carpeta + "/publicaRSA";
            String privateKeyPath = carpeta + "/privadaRSA";

            Files.write(Paths.get(publicKeyPath), llaves[1].getEncoded());
            Files.write(Paths.get(privateKeyPath), llaves[0].getEncoded());

            System.out.println("Las llaves se han guardado correctamente en la carpeta 'llaves'.");

        } catch (IOException e) {
            System.out.println("Ocurrió un error al guardar las llaves.");
            e.printStackTrace();
        }
    }
	
	public static Key leerLlave(String tipoLlave) {
        try {
            String filePath = tipoLlave.equalsIgnoreCase("publica") ? "llaves/publicaRSA" : "llaves/privadaRSA";
            byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if (tipoLlave.equalsIgnoreCase("publica")) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                return keyFactory.generatePublic(keySpec);
            }
            
            else if (tipoLlave.equalsIgnoreCase("privada")) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                return keyFactory.generatePrivate(keySpec);
            } 
        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo de la llave.");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error al generar la llave a partir del archivo.");
            ((Throwable) e).printStackTrace();
        }
        return null;
    }
	
	public static byte[] firmarValores(BigInteger p, BigInteger g, BigInteger gx, PrivateKey privateKey){
        Signature firma;
		try {
			firma = Signature.getInstance("SHA1withRSA");
			firma.initSign(privateKey);

	        firma.update(p.toByteArray());
	        firma.update(g.toByteArray());
	        firma.update(gx.toByteArray());

	        return firma.sign();
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public static boolean verificarFirma(BigInteger p, BigInteger g, BigInteger gx, PublicKey publicKey, byte[] firma){
        
		Signature verificador;
		try {
			verificador = Signature.getInstance("SHA1withRSA");
			verificador.initVerify(publicKey);

	        verificador.update(p.toByteArray());
	        verificador.update(g.toByteArray());
	        verificador.update(gx.toByteArray());

	        boolean esValido = verificador.verify(firma);

	        if (esValido) {
	            return true;
	        } else {
	            return false;
	        }
		} catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
			e.printStackTrace();
			return false;
		}
        
    }
	
	
}
