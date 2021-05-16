package com.example.shopifychallenge;

import javax.annotation.Resource;

import com.example.shopifychallenge.services.ImageStorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopifyChallengeApplication implements CommandLineRunner {

	@Resource
	ImageStorageService imageStorageService;

	public static void main(String[] args) {
		SpringApplication.run(ShopifyChallengeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		imageStorageService.initBaseFolder();
	}
}
