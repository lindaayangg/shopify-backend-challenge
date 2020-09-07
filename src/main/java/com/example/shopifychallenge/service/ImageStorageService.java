package com.example.shopifychallenge.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    void initBaseFolder() throws IOException;

    void initUserFolder(Long userId) throws IOException;

    void saveImage(MultipartFile image, Long imageId, Long userId) throws Exception;

    Resource getImage(String imageName, Long imageId, Long userId) throws Exception;

    void deleteImage(String imageName, Long imageId, Long userId) throws IOException;

    void deleteUserImages(Long userId) throws IOException;
}
