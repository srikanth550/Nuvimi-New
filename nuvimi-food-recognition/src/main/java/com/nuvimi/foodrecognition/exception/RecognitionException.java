package com.nuvimi.foodrecognition.exception;

/**
 * Thrown when the upstream Vision API call fails or returns something we
 * cannot make sense of (bad image, network failure, quota exceeded, etc.).
 */
public class RecognitionException extends RuntimeException {

    public RecognitionException(String message) {
        super(message);
    }

    public RecognitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
