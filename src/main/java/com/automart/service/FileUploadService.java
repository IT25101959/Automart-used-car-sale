package com.automart.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    /**
     * Upload a multipart image file.
     * Writes to both the source repository folder (for persistence) and target/classes directory (for immediate loading).
     *
     * @param file The multipart file to upload
     * @param subfolder Folder under static/uploads/ (e.g., "vehicles", "spareparts")
     * @return Public relative path to store in the database (e.g., "/uploads/vehicles/uuid-file.png")
     * @throws IllegalArgumentException if the file is invalid
     */
    public String saveFile(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save an empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename.");
        }

        // Validate file extension
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file format. Only JPG, JPEG, PNG, and WEBP are allowed.");
        }

        // Generate unique name
        String uniqueFilename = UUID.randomUUID().toString() + "-" + sanitizeFilename(originalFilename);

        try {
            // 1. Resolve paths
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            
            // Source static directory path
            Path sourceUploadDir = projectRoot.resolve(Paths.get("src", "main", "resources", "static", "uploads", subfolder));
            // Target output static directory path (for instant preview without restarting)
            Path targetUploadDir = projectRoot.resolve(Paths.get("target", "classes", "static", "uploads", subfolder));

            // Ensure source directories exist
            if (!Files.exists(sourceUploadDir)) {
                Files.createDirectories(sourceUploadDir);
            }
            
            // 2. Save to source static folder
            Path sourceFilePath = sourceUploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), sourceFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 3. Save to target output static folder (if output target class folder exists)
            if (Files.exists(projectRoot.resolve(Paths.get("target", "classes")))) {
                if (!Files.exists(targetUploadDir)) {
                    Files.createDirectories(targetUploadDir);
                }
                Path targetFilePath = targetUploadDir.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return relative public path
            return "/uploads/" + subfolder + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file locally: " + e.getMessage(), e);
        }
    }

    /**
     * Delete an existing file from both source and target output folders.
     *
     * @param relativePath The public relative path (e.g., "/uploads/vehicles/filename.jpg")
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank() || !relativePath.startsWith("/uploads/")) {
            return;
        }

        // Strip leading slash for path resolving
        String normalizedPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        try {
            Path projectRoot = Paths.get(System.getProperty("user.dir"));

            // Resolve absolute paths
            Path sourceFile = projectRoot.resolve(Paths.get("src", "main", "resources", "static")).resolve(normalizedPath);
            Path targetFile = projectRoot.resolve(Paths.get("target", "classes", "static")).resolve(normalizedPath);

            // Delete from source folder
            if (Files.exists(sourceFile)) {
                Files.delete(sourceFile);
            }

            // Delete from target folder
            if (Files.exists(targetFile)) {
                Files.delete(targetFile);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not delete old file " + relativePath + ": " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return filename.substring(lastIndex);
    }

    private String sanitizeFilename(String filename) {
        // Strip out non-alphanumeric characters, except dots and dashes
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
