package com.example.shopifychallenge;

import com.example.shopifychallenge.service.ImageStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

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
