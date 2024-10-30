package Reporte;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Tiempo {

    private static final String FILE_PATH = "documentos/resultadoTiempos.txt";

    public void guardarTiempo(String modo, String ID, Long deltaTime, String tipoCifrado, String etiqueta) {
        String registro = String.format("%s: ID-\"%s\", Evento: \"%s\", Tipo Cifrado: \"%s\", Tiempo: \"%d ms\"",
                capitalize(modo), ID, etiqueta, tipoCifrado, deltaTime);

        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs(); 
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(registro);
                writer.newLine(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
