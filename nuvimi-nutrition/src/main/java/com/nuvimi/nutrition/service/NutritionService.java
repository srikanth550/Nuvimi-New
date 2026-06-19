package com.nuvimi.nutrition.service;

import com.nuvimi.nutrition.client.UsdaFoodDataClient;
import com.nuvimi.nutrition.client.UsdaFoodDataClient.UsdaFood;
import com.nuvimi.nutrition.client.UsdaFoodDataClient.UsdaNutrient;
import com.nuvimi.nutrition.model.MacroNutrients;
import com.nuvimi.nutrition.model.NutrientValue;
import com.nuvimi.nutrition.model.NutritionResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class NutritionService {

    private static final String DATA_SOURCE_NAME = "USDA FoodData Central";

    private final UsdaFoodDataClient usdaClient;

    public NutritionService(UsdaFoodDataClient usdaClient) {
        this.usdaClient = usdaClient;
    }

    /**
     * Looks up nutrition facts for a food by name. Results are cached per
     * (lowercased, trimmed) query so repeated searches for the same item
     * don't burn through the USDA rate limit.
     */
    @Cacheable(value = "nutritionSearch", key = "#query.toLowerCase().trim()")
    public NutritionResponse search(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Please provide a food name to search for.");
        }

        UsdaFood food = usdaClient.searchBestMatch(query.trim());
        List<UsdaNutrient> nutrients = food.foodNutrients() == null ? List.of() : food.foodNutrients();

        double calories = findCalories(nutrients);
        MacroNutrients macros = new MacroNutrients(
                findGrams(nutrients, "protein"),
                findGrams(nutrients, "total lipid"),
                findGrams(nutrients, "carbohydrate"),
                findGrams(nutrients, "fiber"),
                findGrams(nutrients, "sugars")
        );

        List<NutrientValue> vitamins = nutrients.stream()
                .filter(n -> NutrientClassifier.classify(n.nutrientName()) == NutrientClassifier.Category.VITAMIN)
                .filter(n -> n.value() != null && n.value() > 0)
                .map(this::toNutrientValue)
                .sorted(Comparator.comparing(NutrientValue::name))
                .toList();

        List<NutrientValue> minerals = nutrients.stream()
                .filter(n -> NutrientClassifier.classify(n.nutrientName()) == NutrientClassifier.Category.MINERAL)
                .filter(n -> n.value() != null && n.value() > 0)
                .map(this::toNutrientValue)
                .sorted(Comparator.comparing(NutrientValue::name))
                .toList();

        String sourceUrl = "https://fdc.nal.usda.gov/food-details/" + food.fdcId() + "/nutrients";

        return new NutritionResponse(
                cleanName(food.description()),
                "Per 100g",
                calories,
                macros,
                vitamins,
                minerals,
                DATA_SOURCE_NAME,
                sourceUrl
        );
    }

    private NutrientValue toNutrientValue(UsdaNutrient n) {
        return new NutrientValue(cleanName(n.nutrientName()), round(n.value()), n.unitName());
    }

    private double findCalories(List<UsdaNutrient> nutrients) {
        return nutrients.stream()
                .filter(n -> n.nutrientName().toLowerCase(Locale.ROOT).startsWith("energy"))
                .filter(n -> "KCAL".equalsIgnoreCase(n.unitName()))
                .findFirst()
                .map(UsdaNutrient::value)
                .map(this::round)
                .orElse(0.0);
    }

    private Double findGrams(List<UsdaNutrient> nutrients, String namePrefix) {
        Optional<UsdaNutrient> match = nutrients.stream()
                .filter(n -> n.nutrientName().toLowerCase(Locale.ROOT).startsWith(namePrefix))
                .findFirst();
        return match.map(UsdaNutrient::value).map(this::round).orElse(null);
    }

    private double round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    /** USDA descriptions are written in title case with trailing commas, e.g. "Bananas, raw". */
    private String cleanName(String raw) {
        return raw == null ? "" : raw.trim();
    }
}
