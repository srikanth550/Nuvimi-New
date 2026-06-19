package com.nuvimi.nutrition.exception;

/**
 * Thrown when the call to USDA FoodData Central fails outright (network
 * issue, rate limit, bad response shape, etc.).
 */
public class NutritionLookupException extends RuntimeException {

    public NutritionLookupException(String message) {
        super(message);
    }

    public NutritionLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
