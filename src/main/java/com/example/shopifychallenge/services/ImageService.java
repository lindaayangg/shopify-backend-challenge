package com.example.shopifychallenge.services;

import com.example.shopifychallenge.dtos.ImageInventoryDto;
import com.example.shopifychallenge.exceptions.BadRequestException;
import com.example.shopifychallenge.exceptions.ForbiddenException;
import com.example.shopifychallenge.exceptions.InternalErrorException;
import com.example.shopifychallenge.exceptions.RecordNotFoundException;
import com.example.shopifychallenge.models.Image;

import java.util.List;

public interface ImageService {

    void updateImageInventory(Image image, Long userId, ImageInventoryDto imageInventoryDto) throws ForbiddenException;

    double buyImage(Image image, int amount) throws BadRequestException;

    void deleteUserImagesById(List<Long> imageIds, Long userId) throws RecordNotFoundException, ForbiddenException, InternalErrorException;

    void deleteAllUserImages(Long userId) throws RecordNotFoundException, InternalErrorException;
}
