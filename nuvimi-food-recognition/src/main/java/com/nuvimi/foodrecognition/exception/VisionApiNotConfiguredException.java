package com.nuvimi.foodrecognition.exception;

/**
 * Thrown when a recognition request arrives but no Vision API key has been
 * configured yet. Caught by {@link GlobalExceptionHandler} and turned into a
 * 503 response that tells the caller how to fix it, rather than a confusing
 * stack trace.
 */
public class VisionApiNotConfiguredException extends RuntimeException {

    public VisionApiNotConfiguredException(String message) {
        super(message);
    }
}
