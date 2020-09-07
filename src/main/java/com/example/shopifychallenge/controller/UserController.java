package com.example.shopifychallenge.controller;

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
                             .body("User " +  user.getName() + " created successfully!");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User with ID: " + userId + " not found.");
        }
        return ResponseEntity.ok()
                             .body(user.get());
    }
}
