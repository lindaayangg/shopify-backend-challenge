package com.example.shopifychallenge.controllers;

import com.example.shopifychallenge.dtos.ImageInventoryDto;
import com.example.shopifychallenge.exceptions.BadRequestException;
import com.example.shopifychallenge.exceptions.ForbiddenException;
import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.models.User;
import com.example.shopifychallenge.repositories.ImageRepository;
import com.example.shopifychallenge.repositories.UserRepository;
import com.example.shopifychallenge.services.ImageService;
import com.example.shopifychallenge.services.ImageStorageService;
import com.example.shopifychallenge.enums.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    ImageService imageService;

    @GetMapping("/images/{imageId}")
    public ResponseEntity<Resource> getImageById(@PathVariable Long imageId, @RequestParam Long userId) throws RecordNotFoundException {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            throw new RecordNotFoundException("Image with ID: " + imageId + " not found.");
        }

        if (image.get().getPermission().equals(Permission.PRIVATE) && !image.get().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("No permission to access image with ID: " + imageId + ".");
        }

        Resource resource = imageStorageService.getImage(image.get().getName(), imageId, userId);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/jpeg")).body(resource);
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadImages(@RequestParam Long userId, @RequestParam List<MultipartFile> images,
                                               @RequestParam Permission permission) throws BadRequestException, RecordNotFoundException, InternalErrorException {
        if (images.isEmpty()) {
            throw new BadRequestException("No image to upload found in the request.");
        }

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new RecordNotFoundException("User with ID: " + userId + " not found.");
        }

        imageStorageService.saveImages(images, permission, userId);

        return ResponseEntity.ok().body(images.size() + " image(s) uploaded successfully!");
    }

    @PatchMapping("/images/{imageId}")
    public ResponseEntity<String> updateImageInventory(@RequestParam Long userId, @PathVariable Long imageId,
                                                       @RequestBody ImageInventoryDto imageInventoryDto) throws RecordNotFoundException {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            throw new RecordNotFoundException("Image with ID: " + imageId + " not found.");
        }

        imageService.updateImageInventory(image.get(), userId, imageInventoryDto);

        return ResponseEntity.ok()
            .body("Price for image with ID: " + imageId + " updated to " + imageInventoryDto.getPrice() + ".\n"
                + "Discount for image with ID: " + imageId + " updated to " + imageInventoryDto.getDiscount() + ".\n"
                + "Inventory for image with ID: " + imageId + " updated to " + imageInventoryDto.getAmount() + ".");
    }

    @DeleteMapping("/images")
    public ResponseEntity<String> deleteImages(@RequestParam(required = false) List<Long> imageIds,
                                               @RequestParam Long userId) throws RecordNotFoundException, ForbiddenException, InternalErrorException {
        // if no imageIds, then delete all of the user's images
        if (imageIds == null) {
            imageService.deleteAllUserImages(userId);
            return ResponseEntity.ok().body("All images for user with ID: " + userId + " deleted successfully!");
        }

        // delete specific image
        imageService.deleteUserImagesById(imageIds, userId);
        return ResponseEntity.ok().body("Images deleted successfully!");
    }

    @PostMapping("/buy/{imageId}")
    public ResponseEntity<String> buyImage(@PathVariable Long imageId, @RequestParam Integer amount) throws RecordNotFoundException, BadRequestException {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            throw new RecordNotFoundException("Image with ID: " + imageId + " not found.");
        }

        double roundedTotal = imageService.buyImage(image.get(), amount);

        return ResponseEntity.ok().body("Purchased " + amount + " images with image ID: " + imageId + " for a total of $" + roundedTotal + ".");
    }
}
