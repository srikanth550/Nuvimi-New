package com.nuvimi.nutrition.exception;

/**
 * Thrown when FoodData Central has no results for the given search term.
 */
public class FoodNotFoundException extends RuntimeException {

    public FoodNotFoundException(String query) {
        super("No nutrition data found for \"" + query + "\". Try a simpler or more common name.");
    }
}
