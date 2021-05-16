package com.example.shopifychallenge.services;

import com.example.shopifychallenge.dtos.ImageInventoryDto;
import com.example.shopifychallenge.exceptions.BadRequestException;
import com.example.shopifychallenge.exceptions.ForbiddenException;
import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.models.User;
import com.example.shopifychallenge.repositories.ImageRepository;
import com.example.shopifychallenge.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Image updateImageInventory(Image image, Long userId, ImageInventoryDto imageInventoryDto) throws ForbiddenException {
        if (!image.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("No permission to modify image with ID: " + image.getId() + ".");
        }
        image.setPrice(imageInventoryDto.getPrice());
        image.setDiscount(imageInventoryDto.getDiscount());
        image.setAmount(imageInventoryDto.getAmount());

        return imageRepository.save(image);
    }

    @Override
    public double buyImage(Image image, int amount) throws BadRequestException {
        User owner = image.getOwner();

        if (image.getAmount() == null || image.getAmount() < amount) {
            throw new BadRequestException("Not enough inventory found for image with ID: " + image.getId() + ".");
        }

        image.setAmount(image.getAmount() - amount);

        double total = amount * image.getPrice() * ((100 - image.getDiscount()) / 100.0);
        double roundedTotal = Math.round(total * 100.0) / 100.0;
        owner.setBalance(owner.getBalance() + roundedTotal);

        imageRepository.save(image);
        userRepository.save(owner);

        return roundedTotal;
    }

    @Override
    public void deleteUserImagesById(List<Long> imageIds, Long userId) throws RecordNotFoundException, ForbiddenException, InternalErrorException {
        for (Long imageId : imageIds) {
            Optional<Image> image = imageRepository.findById(imageId);
            if (!image.isPresent()) {
                throw new RecordNotFoundException("Image with ID: " + imageId + " not found.");
            }

            if (!image.get().getOwner().getId().equals(userId)) {
                throw new ForbiddenException("No permission to delete image with ID: " + imageId + ".");
            }

            imageStorageService.deleteImage(image.get().getName(), imageId, userId);
            imageRepository.delete(image.get());
        }
    }

    @Override
    public void deleteAllUserImages(Long userId) throws RecordNotFoundException, InternalErrorException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new RecordNotFoundException("User with ID: " + userId + " not found.");
        }

        imageRepository.deleteAllByOwner(user.get());
        imageStorageService.deleteUserImages(userId);
    }
}
