package com.example.shopifychallenge.services;

import com.example.shopifychallenge.dtos.ImageInventoryDto;
import com.example.shopifychallenge.enums.Permission;
import com.example.shopifychallenge.models.Image;
import com.example.shopifychallenge.models.User;
import com.example.shopifychallenge.repositories.ImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private final ImageService imageService = new ImageServiceImpl();

    @Test
    public void update_image_inventory_updates_price_discount_amount() {
        final User user = new User(1L, "Linda", 0.0, new ArrayList<>());
        final Image image = new Image(1L, user, "hamster.jpg", Permission.PRIVATE, 10.0, 0, 5);
        final ImageInventoryDto imageInventoryDto = new ImageInventoryDto(20.0, 20, 10);

        given(imageRepository.save(image)).willAnswer(invocation -> invocation.getArgument(0));

        Image updatedImage = imageService.updateImageInventory(image, user.getId(), imageInventoryDto);

        assertThat(updatedImage).isNotNull();
        assertThat(updatedImage.getPrice()).isEqualTo(20.0);
        assertThat(updatedImage.getDiscount()).isEqualTo(20);
        assertThat(updatedImage.getAmount()).isEqualTo(10);

        verify(imageRepository).save(any(Image.class));
    }
}
