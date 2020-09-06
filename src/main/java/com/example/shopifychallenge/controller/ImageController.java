package com.example.shopifychallenge.controller;

import com.example.shopifychallenge.model.Image;
import com.example.shopifychallenge.model.User;
import com.example.shopifychallenge.repository.ImageRepository;
import com.example.shopifychallenge.repository.UserRepository;
import com.example.shopifychallenge.service.ImageStorageService;
import com.example.shopifychallenge.utils.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class ImageController {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ImageStorageService imageStorageService;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/images/{imageId}")
    public ResponseEntity<?> getImageById(@PathVariable Long imageId, @RequestParam Long userId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Image with ID: " + imageId + " not found.");
        }
        if (image.get().getPermission().equals(Permission.PRIVATE) && !image.get().getOwner().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("No permission to access image with ID: " + imageId + ".");
        }

        Resource resource;
        try {
            resource = imageStorageService.getImage(image.get().getName(), imageId, userId);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("File with userId: " + userId + " and imageId: " + imageId + " not found");
        }

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("image/jpeg"))
                             .body(resource);
    }

    @PostMapping("/images")
    public ResponseEntity<?> uploadImages(@RequestParam Long userId,
                                          @RequestParam List<MultipartFile> images,
                                          @RequestParam Permission permission) {
        if (images.size() == 0) {
            return ResponseEntity.badRequest()
                                 .body("No image to upload found in the request.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
        for (MultipartFile image : images) {
            Image imageToSave = new Image();
            imageToSave.setOwner(userRepository.getOne(userId));
            imageToSave.setName(image.getOriginalFilename());
            imageToSave.setPermission(permission);
            imageToSave = imageRepository.save(imageToSave);
            try {
                imageStorageService.saveImage(image, imageToSave.getId(), userId);
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Failed to upload image(s).");
            }
        }
        return ResponseEntity.ok().body(images.size() + " image(s) uploaded successfully!");
    }

    @DeleteMapping("/images")
    public ResponseEntity<?> deleteImages(@RequestParam(required = false) List<Long> imageIds, @RequestParam Long userId) {
        // if no imageIds, then delete all of the user's images
        if (imageIds == null) {
            return deleteAllUserImages(userId);
        }
        // if imageIds given, then delete only the specified images if the user has permission
        return deleteUserImagesById(imageIds, userId);
    }

    private ResponseEntity<?> deleteUserImagesById(@RequestParam(required = false) List<Long> imageIds,
                                                   @RequestParam Long userId) {
        for (Long imageId : imageIds) {
            Optional<Image> image = imageRepository.findById(imageId);
            if (!image.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("Image with ID: " + imageId + " not found.");
            }
            if (!image.get().getOwner().getId().equals(userId)) {
                return ResponseEntity.badRequest()
                                     .body("No permission to delete image with ID: " + imageId + ".");
            }
            try {
                imageStorageService.deleteImage(image.get().getName(), imageId, userId);
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Failed to delete image with ID: " + imageId + ".");
            }
            imageRepository.delete(image.get());
        }
        return ResponseEntity.ok()
                             .body("Images deleted successfully!");
    }

    private ResponseEntity<?> deleteAllUserImages(@RequestParam Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
        imageRepository.deleteAllByOwner(user.get());
        try {
            imageStorageService.deleteUserImages(userId);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to delete images for user with ID: " + userId + ".");
        }
        return ResponseEntity.ok()
                             .body("Images for user with ID: " + userId + " deleted successfully!");
    }

    @DeleteMapping("/images/all")
    public ResponseEntity<?> deleteAllImages() {
        imageRepository.deleteAll();
        try {
            imageStorageService.deleteAllImages();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to delete all images.");
        }
        return ResponseEntity.ok()
                             .body("All images deleted successfully!");
    }
}
