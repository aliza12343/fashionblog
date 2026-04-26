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
            // Give every image a unique name so they don't overwrite each other
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Save the file to the 'uploads' folder
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}
