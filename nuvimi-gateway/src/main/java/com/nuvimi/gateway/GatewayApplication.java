package com.nuvimi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Nuvimi API Gateway.
 *
 * <p>Single entry point for the Angular frontend. Routes incoming HTTP requests
 * to the correct downstream microservice (food-recognition or nutrition) using
 * Eureka-based service discovery and load balancing, and applies a shared CORS
 * policy so the SPA running on a different origin can call the API.</p>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
