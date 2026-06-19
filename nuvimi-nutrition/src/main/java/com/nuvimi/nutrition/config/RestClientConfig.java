package com.nuvimi.nutrition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * A plain {@link RestClient} used for calling the external USDA FoodData
 * Central API. Not load-balanced - this is third-party internet traffic, not
 * a call to another Nuvimi microservice.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient usdaRestClient(UsdaProperties usdaProperties) {
        return RestClient.builder()
                .baseUrl(usdaProperties.baseUrl())
                .build();
    }
}
