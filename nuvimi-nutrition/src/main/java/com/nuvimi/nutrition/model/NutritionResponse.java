package com.nuvimi.nutrition.model;

import java.util.List;

/**
 * Full nutrition profile for one matched food item.
 *
 * @param foodName            the matched food's USDA description, e.g. "Bananas, raw"
 * @param servingDescription  what the values below are measured per, e.g. "Per 100g"
 * @param calories            kcal per serving
 * @param macros              protein/fat/carbs/fiber/sugar
 * @param vitamins            all vitamin readings found for this food
 * @param minerals            all mineral readings found for this food
 * @param dataSource          human-readable name of the data provider
 * @param sourceUrl           a link back to the public FoodData Central entry
 */
public record NutritionResponse(
        String foodName,
        String servingDescription,
        double calories,
        MacroNutrients macros,
        List<NutrientValue> vitamins,
        List<NutrientValue> minerals,
        String dataSource,
        String sourceUrl
) {
}
