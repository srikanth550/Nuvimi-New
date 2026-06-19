package com.nuvimi.foodrecognition.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code nuvimi.vision.*} properties so the API key and endpoint can
 * be supplied via environment variables instead of being hard-coded.
 *
 * @param apiKey   Google Cloud Vision API key. Read from the {@code GOOGLE_VISION_API_KEY}
 *                 environment variable - see the project README for setup steps.
 * @param endpoint Base URL of the Vision API REST endpoint.
 */
@ConfigurationProperties(prefix = "nuvimi.vision")
public record VisionProperties(String apiKey, String endpoint) {

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !"changeme".equalsIgnoreCase(apiKey);
    }
}
