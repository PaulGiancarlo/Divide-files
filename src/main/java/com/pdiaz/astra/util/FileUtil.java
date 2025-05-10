package com.pdiaz.astra.util;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utilidad para combinar archivos (esta clase se proporciona como referencia
 * para la implementación en Java de la funcionalidad que se haría con copy /b)
 */
public class FileUtil {

    /**
     * Combina múltiples archivos en uno solo
     *
     * @param outputFile El archivo de salida
     * @param inputFiles Lista de archivos de entrada
     * @throws IOException Si hay un error de E/S
     */
    public static void mergeFiles(String outputFile, List<String> inputFiles) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            for (String file : inputFiles) {
                Files.copy(Paths.get(file), bos);
            }
        }
    }

}
