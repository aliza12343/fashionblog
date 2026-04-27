package org.example.capstone2.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // This is the folder where images will be saved
    private final Path root = Paths.get("uploads");

    public FileStorageService() {
        try {
            // Create the 'uploads' folder if it doesn't exist
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String save(MultipartFile file) {
        try {
            String original = file.getOriginalFilename();
            String extension = "";
            if (original != null && original.contains(".")) {
                String ext = original.substring(original.lastIndexOf('.')).toLowerCase();
                if (ext.matches("\\.(jpg|jpeg|png|gif|webp)")) {
                    extension = ext;
                }
            }
            // Use only a UUID filename — never the original name — to prevent path traversal
            String fileName = UUID.randomUUID().toString() + extension;
            Path target = this.root.toAbsolutePath().normalize().resolve(fileName);
            if (!target.startsWith(this.root.toAbsolutePath().normalize())) {
                throw new RuntimeException("Cannot store file outside the upload directory.");
            }
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
