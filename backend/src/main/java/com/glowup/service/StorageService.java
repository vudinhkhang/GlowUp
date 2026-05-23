package com.glowup.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService(@Value("${glowup.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    /**
     * Stores a multipart file (e.g. original upload) and returns its relative url path.
     */
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            String extension = getFileExtension(file.getOriginalFilename());
            String newFilename = UUID.randomUUID().toString() + (extension.isEmpty() ? ".png" : extension);
            
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
            
            // Security check
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            Files.copy(file.getInputStream(), destinationFile);
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    /**
     * Stores raw bytes (e.g. from Python background remover response) and returns relative url path.
     */
    public String storeBytes(byte[] bytes, String originalFilename) {
        try {
            if (bytes == null || bytes.length == 0) {
                throw new RuntimeException("Failed to store empty byte array.");
            }
            String extension = ".png"; // background removed files are always PNG
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
            
            // Security check
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            try (FileOutputStream fos = new FileOutputStream(destinationFile.toFile())) {
                fos.write(bytes);
            }
            
            return "/uploads/" + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store byte file.", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastIndex = filename.lastIndexOf('.');
        return (lastIndex == -1) ? "" : filename.substring(lastIndex);
    }
}
