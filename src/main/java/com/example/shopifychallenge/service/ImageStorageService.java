package com.example.shopifychallenge.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    public void initBaseFolder() throws IOException;

    public void initUserFolder(Long userId) throws IOException;

    public void saveImage(MultipartFile image, Long imageId, Long userId) throws Exception;

    public Resource getImage(String imageName, Long imageId, Long userId) throws Exception;

    public void deleteImage(String imageName, Long imageId, Long userId) throws IOException;

    public void deleteUserImages(Long userId) throws IOException;

    public void deleteAllImages() throws IOException;
}
