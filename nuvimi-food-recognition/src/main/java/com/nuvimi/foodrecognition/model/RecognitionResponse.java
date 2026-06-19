package com.nuvimi.foodrecognition.model;

import java.util.List;

/**
 * Result of identifying a fruit, vegetable, or ingredient from a photo.
 *
 * @param detectedName the best-guess common name of the item, ready to be
 *                      passed straight into the nutrition service search
 * @param confidence    a 0.0-1.0 score reported by the vision model
 * @param alternatives  other plausible names, offered in case the top guess is wrong
 */
public record RecognitionResponse(String detectedName, double confidence, List<String> alternatives) {
}
