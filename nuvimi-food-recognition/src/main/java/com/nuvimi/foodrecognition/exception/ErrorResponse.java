package com.nuvimi.foodrecognition.exception;

/**
 * Consistent JSON error shape returned to the gateway/frontend for any failure.
 */
public record ErrorResponse(String error, String message) {
}
