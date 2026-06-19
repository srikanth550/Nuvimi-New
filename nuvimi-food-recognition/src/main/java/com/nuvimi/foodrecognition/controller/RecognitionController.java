package com.nuvimi.foodrecognition.controller;

import com.nuvimi.foodrecognition.model.RecognitionResponse;
import com.nuvimi.foodrecognition.service.FoodRecognitionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
@RequestMapping("/api/recognition")
public class RecognitionController {

    private final FoodRecognitionService recognitionService;

    public RecognitionController(FoodRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    /**
     * Accepts a photo (taken or uploaded by the user) and returns the most
     * likely fruit/vegetable/ingredient name detected in it.
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecognitionResponse recognizeImage(@RequestPart("image") MultipartFile image) {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Please attach an image file.");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("The uploaded file must be an image.");
        }

        try {
            return recognitionService.identify(image.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read the uploaded image.", e);
        }
    }
}
