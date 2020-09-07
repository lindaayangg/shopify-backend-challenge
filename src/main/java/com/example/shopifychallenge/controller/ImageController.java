package com.example.shopifychallenge.controller;

import com.example.shopifychallenge.dto.ShopImageDto;
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
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/images/{imageId}")
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
                                 .body("Image with user ID: " + userId + " and image ID: " + imageId + " not found.");
        }

        return ResponseEntity.ok()
                             .contentType(MediaType.parseMediaType("image/jpeg"))
                             .body(resource);
    }

    @PostMapping("/images/{imageId}")
    public ResponseEntity<?> setImageShopInfo(@RequestParam Long userId,
                                              @PathVariable Long imageId,
                                              @RequestBody ShopImageDto shopImageDto) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Image with ID: " + imageId + " not found.");
        }

        Image imageResult = image.get();
        if (!imageResult.getOwner().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("No permission to modify image with ID: " + imageId + ".");
        }
        imageResult.setPrice(shopImageDto.getPrice());
        imageResult.setDiscount(shopImageDto.getDiscount());
        imageResult.setAmount(shopImageDto.getAmount());
        imageRepository.save(imageResult);
        return ResponseEntity.ok()
                             .body("Price for image with ID: " + imageId + " updated to " + shopImageDto.getPrice() + ".\n" +
                                           "Discount for image with ID: " + imageId + " updated to " + shopImageDto.getDiscount() + ".\n" +
                                           "Inventory for image with ID: " + imageId + " updated to " + shopImageDto.getAmount() + ".");
    }

    @PostMapping("/images/{imageId}/buy")
    public ResponseEntity<?> buyImage(@PathVariable Long imageId,
                                      @RequestParam Integer amount) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Image with ID: " + imageId + " not found.");
        }

        User owner = image.get().getOwner();
        Image imageResult = image.get();
        if (imageResult.getAmount() == null || imageResult.getAmount() < amount) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Not enough inventory found for image with ID: " + imageId + ".");
        }

        imageResult.setAmount(imageResult.getAmount() - amount);
        double total = amount * imageResult.getPrice() * ((100 - imageResult.getDiscount()) / 100.0);
        double roundedTotal = Math.round(total * 100.0) / 100.0;
        owner.setBalance(owner.getBalance() + roundedTotal);
        imageRepository.save(imageResult);
        userRepository.save(owner);
        return ResponseEntity.ok()
                             .body("Purchased " + amount + " images with image ID: " + imageId + " for a total of $" + roundedTotal + ".");
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
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
                             .body("All images for user with ID: " + userId + " deleted successfully!");
    }
}
