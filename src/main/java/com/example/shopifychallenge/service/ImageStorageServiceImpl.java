package com.example.shopifychallenge.service;

import javassist.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final String base = "images";

    @Override
    public void initBaseFolder() throws IOException {
        Path basePath = Paths.get(base);
        if (!Files.exists(basePath)) {
            Files.createDirectory(basePath);
        }
    }

    @Override
    public void initUserFolder(Long userId) throws IOException {
        Path userPath = Paths.get(base + "/user_" + userId);
        if (!Files.exists(userPath)) {
            Files.createDirectory(userPath);
        }
    }

    @Override
    public void saveImage(MultipartFile image, Long imageId, Long userId) throws Exception {
        Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
        if (!Files.exists(imagePath)) {
            Files.createDirectory(imagePath);
        }
        if (image.getOriginalFilename() == null) {
            throw new Exception("Original file name does not exist.");
        }

        Files.copy(image.getInputStream(), imagePath.resolve(image.getOriginalFilename()));
    }

    @Override
    public Resource getImage(String imageName, Long imageId, Long userId) throws Exception {
        Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
        Path imageFile = imagePath.resolve(imageName);
        Resource resource = new UrlResource(imageFile.toUri());
        if (!resource.exists()) {
            throw new NotFoundException("Image not found");
        }
        return resource;
    }

    @Override
    public void deleteImage(String imageName, Long imageId, Long userId) throws IOException {
        Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
        FileSystemUtils.deleteRecursively(imagePath);
    }

    @Override
    public void deleteUserImages(Long userId) throws IOException {
        Path userPath = Paths.get(base + "/user_" + userId);
        FileSystemUtils.deleteRecursively(userPath);
        initUserFolder(userId);
    }
}
