package org.top.promopacktesting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.top.promopacktesting.model.Question;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.image.upload-dir}")
    private String uploadDir;

    @Value("${app.image.base-url}")
    private String baseUrl;

    public String saveImage(MultipartFile file) throws IOException {

        if (file.isEmpty()) return null;

        if (file.getSize() > Question.MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Размер изображения превышает 5 МБ");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "."
                + StringUtils.getFilenameExtension(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);
        String fullImagePath = baseUrl + fileName;
        System.out.println("Сохранённый путь: " + fullImagePath);

        return fullImagePath;
    }
}