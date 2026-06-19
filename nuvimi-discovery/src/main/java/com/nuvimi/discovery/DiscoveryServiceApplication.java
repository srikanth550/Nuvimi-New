package com.nuvimi.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Nuvimi Discovery Service.
 *
 * <p>Acts as the Eureka service registry for the Nuvimi platform. Every other
 * microservice (gateway, food-recognition, nutrition) registers itself here on
 * startup, and the gateway uses this registry to load-balance requests to the
 * correct service instances.</p>
 *
 * <p>Dashboard available at: <a href="http://localhost:8761">http://localhost:8761</a></p>
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
