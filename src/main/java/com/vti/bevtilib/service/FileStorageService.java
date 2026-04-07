package com.vti.bevtilib.service;

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

    private static final String UPLOAD_DIR = "uploads";

    /**
     * Lưu file vào thư mục uploads/{subDir}/
     * @return URL tương đối: /uploads/{subDir}/{uuid}.ext
     */
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File không được để trống.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IOException("File không hợp lệ.");
        }

        // Validate extension
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            throw new IOException("Chỉ chấp nhận file ảnh (jpg, jpeg, png, gif, webp).");
        }

        // Create upload directory
        Path uploadPath = Paths.get(UPLOAD_DIR, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Prevent path traversal
        if (!filePath.normalize().startsWith(uploadPath.normalize())) {
            throw new IOException("Đường dẫn file không hợp lệ.");
        }

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + UPLOAD_DIR + "/" + subDir + "/" + uniqueFilename;
    }

    /**
     * Xóa file từ đường dẫn tương đối
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;

        try {
            // Remove leading slash
            String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
            Path filePath = Paths.get(cleanPath);

            // Only delete files within uploads directory
            if (filePath.normalize().startsWith(Paths.get(UPLOAD_DIR).normalize())) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // Log but don't throw - file deletion failure shouldn't break the flow
            System.err.println("Không thể xóa file: " + relativePath + " - " + e.getMessage());
        }
    }
}
