package Almacenamiento;

import java.util.HashMap;
import java.util.Random;

public class Bodega {

    private String nombre = "Cuchau";
    private HashMap<String, Paquete> paquetes;

    public Bodega() {
        this.paquetes = new HashMap<String, Paquete>();
        cargarBodega();
    }

    private void cargarBodega() {

    	String[] estados = {"ENOFICINA", "RECOGIDO", "ENCLASIFICACION", "DESPACHADO", "ENENTREGA", "ENTREGADO", "DESCONOCIDO"};
        Random random = new Random();

        for (int i = 1; i <= 32; i++) {
            String estado = estados[random.nextInt(estados.length)]; 
            Paquete paquete = new Paquete( Integer.toString(i), estado); 
            paquetes.put(Integer.toString(i), paquete);
        }
    }
    
    public String obtenerEstadoPaquete(String userId, String paqueteId) {

    	Paquete paquete = paquetes.get(userId);

        if (paquete != null && paquete.getId().equals(paqueteId)) {
            return paquete.getEstado();
        } else {
            return "DESCONOCIDO";
        }
    }

    public String getNombre() {
        return nombre;
    }

    public HashMap<String, Paquete> getPaquetes() {
        return paquetes;
    }
    
    
}
