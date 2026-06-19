package com.nuvimi.foodrecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Nuvimi Food Recognition Service.
 *
 * <p>Accepts a photo of a fruit, vegetable, or ingredient and identifies what it
 * is using an external image-recognition API, so the result can be handed off
 * to the nutrition service for a nutrient lookup.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class FoodRecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodRecognitionApplication.class, args);
    }
}
