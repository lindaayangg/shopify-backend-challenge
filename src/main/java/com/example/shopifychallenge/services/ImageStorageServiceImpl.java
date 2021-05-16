package com.example.shopifychallenge.services;

import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.repositories.ImageRepository;
import com.example.shopifychallenge.repositories.UserRepository;
import com.example.shopifychallenge.enums.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageRepository imageRepository;

    private final String base = "images";

    @Override
    public void initBaseFolder() throws InternalErrorException {
        Path basePath = Paths.get(base);
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectory(basePath);
            } catch (IOException ex) {
                throw new InternalErrorException("Could not initialize base folder.");
            }
        }
    }

    @Override
    public void initUserFolder(Long userId) throws InternalErrorException {
        Path userPath = Paths.get(base + "/user_" + userId);
        if (!Files.exists(userPath)) {
            try {
                Files.createDirectory(userPath);
            } catch (IOException ex) {
                throw new InternalErrorException("Could not initialize folder for user with ID: " + userId);
            }
        }
    }

    @Override
    public void saveImages(List<MultipartFile> images, Permission permission, Long userId) throws InternalErrorException {
        for (MultipartFile image : images) {
            Image imageToSave = new Image();
            imageToSave.setOwner(userRepository.getOne(userId));
            imageToSave.setName(image.getOriginalFilename());
            imageToSave.setPermission(permission);
            imageToSave = imageRepository.save(imageToSave);

            saveImage(image, imageToSave.getId(), userId);
        }
    }

    @Override
    public Resource getImage(String imageName, Long imageId, Long userId) throws RecordNotFoundException {
        Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
        Path imageFile = imagePath.resolve(imageName);

        Resource resource;
        try {
            resource = new UrlResource(imageFile.toUri());
        } catch (MalformedURLException ex) {
            throw new RecordNotFoundException("Image with ID: " + imageId + " not found");
        }

        if (!resource.exists()) {
            throw new RecordNotFoundException("Image with ID: " + imageId + " not found");
        }

        return resource;
    }

    @Override
    public void deleteImage(String imageName, Long imageId, Long userId) throws InternalErrorException {
        Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
        try {
            FileSystemUtils.deleteRecursively(imagePath);
        } catch (IOException ex) {
            throw new InternalErrorException("Could not delete image with ID: " + imageId + ".");
        }
    }

    @Override
    public void deleteUserImages(Long userId) throws InternalErrorException {
        Path userPath = Paths.get(base + "/user_" + userId);
        try {
            FileSystemUtils.deleteRecursively(userPath);
        } catch (IOException ex) {
            throw new InternalErrorException("Could not delete images of user with ID: " + userId + ".");
        }

        initUserFolder(userId);
    }

    private void saveImage(MultipartFile image, Long imageId, Long userId) throws InternalErrorException {
        try {
            Path imagePath = Paths.get(base + "/user_" + userId + "/image_" + imageId);
            if (!Files.exists(imagePath)) {
                Files.createDirectory(imagePath);
            }

            if (image.getOriginalFilename() == null) {
                throw new InternalErrorException("Original file name does not exist.");
            }

            Files.copy(image.getInputStream(), imagePath.resolve(image.getOriginalFilename()));
        } catch (IOException ex) {
            throw new InternalErrorException("Could not save image with ID: " + imageId + ".");
        }
    }
}
