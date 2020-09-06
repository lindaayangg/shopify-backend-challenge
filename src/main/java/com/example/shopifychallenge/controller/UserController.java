package com.example.shopifychallenge.controller;

import com.example.shopifychallenge.dto.ShopImageDto;
import com.example.shopifychallenge.model.Image;
import com.example.shopifychallenge.model.User;
import com.example.shopifychallenge.repository.ImageRepository;
import com.example.shopifychallenge.repository.UserRepository;
import com.example.shopifychallenge.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ImageStorageService imageStorageService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        user.setBalance(0.0);
        userRepository.save(user);
        try {
            imageStorageService.initUserFolder(user.getId());
        } catch (IOException ex) {
            userRepository.delete(user);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("User image folder could not be created.");
        }
        return ResponseEntity.ok()
                             .body("User created successfully!");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
        return ResponseEntity.ok()
                             .body(user.get());
    }

    @PostMapping("/users/{userId}/{imageId}")
    public ResponseEntity<?> setImageShopInfo(@PathVariable Long userId,
                                              @PathVariable Long imageId,
                                              @RequestBody ShopImageDto shopImageDto) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
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

    @PostMapping("/users/{userId}/{imageId}/buy")
    public ResponseEntity<?> buyImage(@PathVariable Long userId,
                                      @PathVariable Long imageId,
                                      @RequestParam Integer amount) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
        Optional<Image> image = imageRepository.findById(imageId);
        if (!image.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Image with ID: " + imageId + " not found.");
        }

        Image imageResult = image.get();
        User userResult = user.get();
        if (imageResult.getAmount() == null || imageResult.getAmount() < amount) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Not enough inventory found for image with ID: " + imageId + ".");
        }
        imageResult.setAmount(imageResult.getAmount() - amount);
        double total = amount * imageResult.getPrice() * ((100 - imageResult.getDiscount()) / 100.0);
        double roundedTotal = Math.round(total * 100.0) / 100.0;
        userResult.setBalance(userResult.getBalance() + roundedTotal);
        imageRepository.save(imageResult);
        userRepository.save(userResult);
        return ResponseEntity.ok()
                             .body("Purchased " + amount + " images with image ID: " + imageId + " for a total of $" + roundedTotal + ".");
    }
}
