package com.example.shopifychallenge.controllers;

import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.User;
import com.example.shopifychallenge.repositories.ImageRepository;
import com.example.shopifychallenge.repositories.UserRepository;
import com.example.shopifychallenge.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> createUser(@RequestBody User user) throws InternalErrorException {
        user.setBalance(0.0);
        userRepository.save(user);

        try {
            imageStorageService.initUserFolder(user.getId());
        } catch (InternalErrorException ex) {
            userRepository.delete(user);
            throw ex;
        }

        return ResponseEntity.ok().body("User " + user.getName() + " created successfully!");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) throws RecordNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new RecordNotFoundException("User with ID: " + userId + " not found.");
        }

        return ResponseEntity.ok().body(user.get());
    }
}
