package com.nuvimi.foodrecognition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * A plain {@link RestClient} (not load-balanced) used purely for calling the
 * external, third-party Vision API - this traffic never goes through Eureka.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient visionRestClient() {
        return RestClient.builder().build();
    }
}
