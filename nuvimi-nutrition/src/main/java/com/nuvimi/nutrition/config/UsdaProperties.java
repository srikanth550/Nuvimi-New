package com.nuvimi.nutrition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code nuvimi.usda.*} properties.
 *
 * @param apiKey  USDA FoodData Central API key. Defaults to the public DEMO_KEY
 *                (30 requests/hour) - get a free, higher-limit key at
 *                <a href="https://fdc.nal.usda.gov/api-key-signup">fdc.nal.usda.gov/api-key-signup</a>
 *                and export it as the {@code USDA_API_KEY} environment variable.
 * @param baseUrl Base URL of the FoodData Central REST API.
 */
@ConfigurationProperties(prefix = "nuvimi.usda")
public record UsdaProperties(String apiKey, String baseUrl) {
}
