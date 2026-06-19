package com.nuvimi.foodrecognition.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Converts the exceptions this service can throw into clean, predictable JSON
 * responses instead of leaking stack traces to the caller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VisionApiNotConfiguredException.class)
    public ResponseEntity<ErrorResponse> handleNotConfigured(VisionApiNotConfiguredException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("VISION_API_NOT_CONFIGURED", ex.getMessage()));
    }

    @ExceptionHandler(RecognitionException.class)
    public ResponseEntity<ErrorResponse> handleRecognitionFailure(RecognitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("RECOGNITION_FAILED", ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse("IMAGE_TOO_LARGE", "Please upload an image smaller than 10MB."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadInput(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("UNEXPECTED_ERROR", "Something went wrong while identifying the image."));
    }
}
