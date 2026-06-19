package com.nuvimi.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Nuvimi Nutrition Service.
 *
 * <p>Looks up calories, macronutrients, vitamins, and minerals for a food item
 * by name, sourced live from the USDA FoodData Central public API.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@ConfigurationPropertiesScan
public class NutritionApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutritionApplication.class, args);
    }
}
