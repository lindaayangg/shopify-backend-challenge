package com.example.shopifychallenge.services;

import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.enums.Permission;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {
    void initBaseFolder() throws InternalErrorException;

    void initUserFolder(Long userId) throws InternalErrorException;

    void saveImages(List<MultipartFile> images, Permission permission, Long userId) throws InternalErrorException;

    Resource getImage(String imageName, Long imageId, Long userId) throws RecordNotFoundException;

    void deleteImage(String imageName, Long imageId, Long userId) throws InternalErrorException;

    void deleteUserImages(Long userId) throws InternalErrorException;
}
