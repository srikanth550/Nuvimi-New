package com.nuvimi.nutrition.model;

/**
 * A single vitamin or mineral reading, e.g. ("Vitamin C, total ascorbic acid", 8.7, "MG").
 */
public record NutrientValue(String name, double amount, String unit) {
}
