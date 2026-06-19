package com.nuvimi.nutrition.controller;

import com.nuvimi.nutrition.model.NutritionResponse;
import com.nuvimi.nutrition.service.NutritionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final NutritionService nutritionService;

    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    /**
     * Looks up calories, macronutrients, vitamins, and minerals for a food by name.
     * Example: {@code GET /api/nutrition/search?query=banana}
     */
    @GetMapping("/search")
    public NutritionResponse search(@RequestParam String query) {
        return nutritionService.search(query);
    }
}
