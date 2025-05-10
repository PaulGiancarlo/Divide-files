package com.pdiaz.astra.service;


import com.pdiaz.astra.configuration.FileStorageProperties;
import com.pdiaz.astra.model.FileSegment;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio donde se almacenarán los archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(fileName);

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia de ruta no válida " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + fileName, ex);
        }
    }

    public List<FileSegment> splitFile(String fileName, int chunkSize) {
        List<FileSegment> segments = new ArrayList<>();
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);

        try {
            byte[] buffer = new byte[chunkSize * 1024 * 1024]; // Tamaño en MB
            File inputFile = filePath.toFile();

            try (FileInputStream fis = new FileInputStream(inputFile)) {
                int bytesRead;
                int partCounter = 0;

                while ((bytesRead = fis.read(buffer)) > 0) {
                    String partFileName = baseName + "." + partCounter + "." + extension;
                    Path partFilePath = this.fileStorageLocation.resolve(partFileName);

                    try (FileOutputStream fos = new FileOutputStream(partFilePath.toFile())) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    FileSegment segment = new FileSegment();
                    segment.setFileName(partFileName);
                    segment.setPartNumber(partCounter);
                    segment.setSize(bytesRead);
                    segments.add(segment);

                    partCounter++;
                }
            }

            Files.deleteIfExists(filePath);

            return segments;
        } catch (IOException ex) {
            throw new RuntimeException("Error al dividir el archivo " + fileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Archivo no encontrado " + fileName, ex);
        }
    }

    public List<FileSegment> getSegments(String baseName, String extension) {
        List<FileSegment> segments = new ArrayList<>();
        try {
            Files.list(this.fileStorageLocation)
                    .filter(path -> {
                        String filename = path.getFileName().toString();
                        return filename.startsWith(baseName + ".") && filename.endsWith("." + extension);
                    })
                    .sorted((p1, p2) -> {
                        String name1 = p1.getFileName().toString();
                        String name2 = p2.getFileName().toString();

                        int part1 = Integer.parseInt(name1.substring(name1.indexOf(".") + 1, name1.lastIndexOf(".")));
                        int part2 = Integer.parseInt(name2.substring(name2.indexOf(".") + 1, name2.lastIndexOf(".")));

                        return Integer.compare(part1, part2);
                    })
                    .forEach(path -> {
                        String filename = path.getFileName().toString();
                        int partNumber = Integer.parseInt(filename.substring(filename.indexOf(".") + 1, filename.lastIndexOf(".")));

                        FileSegment segment = new FileSegment();
                        segment.setFileName(filename);
                        segment.setPartNumber(partNumber);
                        try {
                            segment.setSize((int) Files.size(path));
                        } catch (IOException e) {
                            segment.setSize(0);
                        }

                        segments.add(segment);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error al listar los segmentos de archivo", e);
        }

        return segments;
    }
}
