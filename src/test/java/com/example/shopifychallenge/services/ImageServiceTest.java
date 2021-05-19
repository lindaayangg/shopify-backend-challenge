package com.example.shopifychallenge.services;

import com.example.shopifychallenge.dtos.ImageInventoryDto;
import com.example.shopifychallenge.enums.Permission;
import com.example.shopifychallenge.exceptions.BadRequestException;
import com.example.shopifychallenge.exceptions.ForbiddenException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.models.User;
import com.example.shopifychallenge.repositories.ImageRepository;
import com.example.shopifychallenge.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    User user;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private final ImageService imageService = new ImageServiceImpl();

    @Mock
    private ImageStorageService imageStorageService;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "Linda", 0.0, new ArrayList<>());
    }

    @Test
    public void updateImageInventory_updates_price_discount_amount_calls_imageRepository() {
        final Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 0, 5);
        final ImageInventoryDto newInventory = new ImageInventoryDto(20.0, 20, 10);

        when(imageRepository.save(image)).thenReturn(image);

        Image updatedImage = imageService.updateImageInventory(image, user.getId(), newInventory);

        verify(imageRepository, times(1)).save(updatedImage);
        assertThat(updatedImage).isNotNull();
        assertThat(updatedImage.getPrice()).isEqualTo(20.0);
        assertThat(updatedImage.getDiscount()).isEqualTo(20);
        assertThat(updatedImage.getAmount()).isEqualTo(10);
    }

    @Test
    public void updateImageInventory_no_permission_throws_exception_does_not_call_imageRepository() {
        final Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 0, 5);
        final ImageInventoryDto newInventory = new ImageInventoryDto(20.0, 20, 10);

        Exception exception = assertThrows(ForbiddenException.class, () -> imageService.updateImageInventory(image, 2L, newInventory));
        assertThat(exception.getMessage()).isEqualTo("No permission to modify image with ID: 1.");

        verify(imageRepository, times(0)).save(image);
    }

    @Test
    public void buyImage_returns_total() {
        Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);
        when(imageRepository.save(image)).thenReturn(image);
        when(userRepository.save(user)).thenReturn(user);

        double total = imageService.buyImage(image, 3);
        double expectedTotal = 3 * 10.0 * ((100 - 10) / 100.0);
        double expectedRoundTotal = Math.round(expectedTotal * 100.0) / 100.0;

        assertThat(total).isEqualTo(expectedRoundTotal);
    }

    @Test
    public void buyImage_updates_owner_balance_and_inventory_and_calls_imageRepository_and_userRepository() {
        Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);
        when(imageRepository.save(image)).thenReturn(image);
        when(userRepository.save(user)).thenReturn(user);

        imageService.buyImage(image, 3);

        verify(imageRepository, times(1)).save(image);
        verify(userRepository, times(1)).save(user);
        assertThat(image.getAmount()).isEqualTo(2);
        assertThat(user.getBalance()).isEqualTo(27.0);
    }

    @Test
    public void buyImage_not_enough_images_throw_exception_and_does_not_call_imageRepository_and_userRepository() {
        Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);

        Exception exception = assertThrows(BadRequestException.class, () -> imageService.buyImage(image, 6));
        assertThat(exception.getMessage()).isEqualTo("Not enough inventory found for image with ID: 1.");

        verify(imageRepository, times(0)).save(image);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void deleteUserImagesById_calls_imageStorageService_and_imageRepository() {
        Image image1 = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);
        Image image2 = new Image(2L, user, "hamster1.jpg", Permission.PRIVATE, 20.0, 0, 10);

        List<Long> imagesToDelete = new ArrayList<>();
        imagesToDelete.add(image1.getId());
        imagesToDelete.add(image2.getId());

        when(imageRepository.findById(image1.getId())).thenReturn(Optional.of(image1));
        when(imageRepository.findById(image2.getId())).thenReturn(Optional.of(image2));

        doNothing().when(imageStorageService).deleteImage(image1.getName(), image1.getId(), user.getId());
        doNothing().when(imageStorageService).deleteImage(image2.getName(), image2.getId(), user.getId());

        imageService.deleteUserImagesById(imagesToDelete, user.getId());

        verify(imageRepository, times(1)).findById(image1.getId());
        verify(imageRepository, times(1)).findById(image2.getId());
        verify(imageStorageService, times(1)).deleteImage(image1.getName(), image1.getId(), user.getId());
        verify(imageStorageService, times(1)).deleteImage(image2.getName(), image2.getId(), user.getId());
        verify(imageRepository, times(1)).delete(image1);
        verify(imageRepository, times(1)).delete(image2);
    }

    @Test
    public void deleteUserImagesById_image_is_not_present() {
        Image image1 = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);

        List<Long> imagesToDelete = new ArrayList<>();
        imagesToDelete.add(image1.getId());

        when(imageRepository.findById(image1.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RecordNotFoundException.class, () -> imageService.deleteUserImagesById(imagesToDelete, user.getId()));
        assertThat(exception.getMessage()).isEqualTo("Image with ID: 1 not found.");

        verify(imageRepository, times(1)).findById(image1.getId());
        verify(imageStorageService, times(0)).deleteImage(image1.getName(), image1.getId(), user.getId());
        verify(imageRepository, times(0)).delete(image1);
    }

    @Test
    public void deleteUserImagesById_no_permission() {
        Image image1 = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 10, 5);

        List<Long> imagesToDelete = new ArrayList<>();
        imagesToDelete.add(image1.getId());

        when(imageRepository.findById(image1.getId())).thenReturn(Optional.of(image1));

        Exception exception = assertThrows(ForbiddenException.class, () -> imageService.deleteUserImagesById(imagesToDelete, 2L));
        assertThat(exception.getMessage()).isEqualTo("No permission to delete image with ID: 1.");

        verify(imageRepository, times(1)).findById(image1.getId());
        verify(imageStorageService, times(0)).deleteImage(image1.getName(), image1.getId(), 2L);
        verify(imageRepository, times(0)).delete(image1);
    }

    @Test
    public void deleteAllUserImages_calls_imageStorageService_and_imageRepository() {
       when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
       doNothing().when(imageStorageService).deleteUserImages(user.getId());

       imageService.deleteAllUserImages(user.getId());

       verify(imageRepository, times(1)).deleteAllByOwner(user);
       verify(imageStorageService, times(1)).deleteUserImages(user.getId());
    }

    @Test
    public void deleteAllUserImages_user_not_present() {
        Exception exception = assertThrows(RecordNotFoundException.class, () -> imageService.deleteAllUserImages(2L));
        assertThat(exception.getMessage()).isEqualTo("User with ID: 2 not found.");

        verify(userRepository, times(1)).findById(2L);
        verify(imageRepository, times(0)).deleteAllByOwner(user);
        verify(imageStorageService, times(0)).deleteUserImages(user.getId());
    }

}
