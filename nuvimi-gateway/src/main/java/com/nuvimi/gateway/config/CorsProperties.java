package com.nuvimi.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Binds {@code nuvimi.cors.allowed-origins} as a proper {@code List<String>}.
 *
 * <p>Note: a YAML list cannot be bound with a plain {@code @Value(...)}
 * placeholder (Spring Boot flattens it into indexed {@code [0]}, {@code [1]}
 * keys internally) - {@code @ConfigurationProperties} is what understands
 * that indexed structure and reassembles it into a {@code List}.</p>
 */
@ConfigurationProperties(prefix = "nuvimi.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
