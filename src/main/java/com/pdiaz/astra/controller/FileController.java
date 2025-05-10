package com.pdiaz.astra.controller;


import com.pdiaz.astra.model.FileSegment;
import com.pdiaz.astra.model.FileUploadResponse;
import com.pdiaz.astra.service.FileService;
import com.pdiaz.astra.util.MimeTypeUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "chunkSize", defaultValue = "1") int chunkSize,
                             Model model) {
        String fileName = fileService.storeFile(file);

        String extension = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);

        List<FileSegment> segments = fileService.splitFile(fileName, chunkSize);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(baseName);
        response.setFileType(extension);
        response.setSize(file.getSize());
        response.setSegments(segments);

        model.addAttribute("file", response);
        return "result";
    }

    @GetMapping("/downloadSegment/{fileName:.+}")
    public ResponseEntity<Resource> downloadSegment(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileService.loadFileAsResource(fileName);

        // Intentar determinar el tipo de contenido
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // El tipo de contenido no se pudo determinar
        }

        // Si no se pudo determinar, intentar con nuestra utilidad
        if (contentType == null) {
            String extension = FilenameUtils.getExtension(fileName);
            contentType = MimeTypeUtils.getMimeTypeForExtension(extension);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/viewSegments/{baseName}/{extension}")
    public String viewSegments(@PathVariable String baseName, @PathVariable String extension, Model model) {
        List<FileSegment> segments = fileService.getSegments(baseName, extension);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(baseName);
        response.setFileType(extension);
        response.setSegments(segments);

        model.addAttribute("file", response);
        return "result";
    }
}