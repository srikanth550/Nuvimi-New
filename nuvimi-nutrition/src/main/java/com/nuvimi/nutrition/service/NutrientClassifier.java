package com.nuvimi.nutrition.service;

import java.util.List;
import java.util.Locale;

/**
 * USDA returns 70+ raw nutrient rows per food, named things like
 * "Vitamin C, total ascorbic acid" or "Calcium, Ca". This classifies each one
 * into a bucket the frontend actually wants to show: a macronutrient, a
 * vitamin, a mineral, or "other" (amino acids, fatty acid breakdowns, etc.,
 * which we don't surface in the UI).
 */
final class NutrientClassifier {

    enum Category { MACRO, VITAMIN, MINERAL, OTHER }

    private static final List<String> MACRO_PREFIXES = List.of(
            "energy", "protein", "total lipid", "carbohydrate", "fiber", "sugars"
    );

    private static final List<String> VITAMIN_KEYWORDS = List.of(
            "vitamin", "thiamin", "riboflavin", "niacin", "folate", "folic acid",
            "pantothenic acid", "biotin", "choline"
    );

    private static final List<String> MINERAL_PREFIXES = List.of(
            "calcium", "iron", "magnesium", "phosphorus", "potassium", "sodium",
            "zinc", "copper", "manganese", "selenium", "fluoride", "iodine",
            "chromium", "molybdenum"
    );

    private NutrientClassifier() {
    }

    static Category classify(String rawName) {
        String name = rawName.toLowerCase(Locale.ROOT);

        if (MACRO_PREFIXES.stream().anyMatch(name::startsWith)) {
            return Category.MACRO;
        }
        if (MINERAL_PREFIXES.stream().anyMatch(name::startsWith)) {
            return Category.MINERAL;
        }
        if (VITAMIN_KEYWORDS.stream().anyMatch(name::contains)) {
            return Category.VITAMIN;
        }
        return Category.OTHER;
    }
}
