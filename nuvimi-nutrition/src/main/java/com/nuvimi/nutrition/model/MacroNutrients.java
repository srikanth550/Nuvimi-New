package com.nuvimi.nutrition.model;

/**
 * Headline macronutrients, in grams (nulls mean the source food had no value
 * reported for that nutrient).
 */
public record MacroNutrients(Double proteinG, Double fatG, Double carbsG, Double fiberG, Double sugarG) {
}
