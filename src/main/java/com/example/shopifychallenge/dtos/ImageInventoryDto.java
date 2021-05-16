package com.example.shopifychallenge.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageInventoryDto {
    private Double price;
    private Integer discount;
    private Integer amount;
}
