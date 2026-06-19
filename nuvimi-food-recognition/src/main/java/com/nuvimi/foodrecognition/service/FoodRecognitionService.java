package com.nuvimi.foodrecognition.service;

import com.nuvimi.foodrecognition.client.GoogleVisionClient;
import com.nuvimi.foodrecognition.client.GoogleVisionClient.VisionLabel;
import com.nuvimi.foodrecognition.config.VisionProperties;
import com.nuvimi.foodrecognition.exception.VisionApiNotConfiguredException;
import com.nuvimi.foodrecognition.model.RecognitionResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Identifies the most likely fruit, vegetable, or ingredient name from a photo.
 *
 * <p>The Vision API returns general-purpose image labels, some of which are
 * broad categories ("Food", "Produce", "Natural foods") rather than the name
 * of the actual item. This service re-ranks the raw labels so a specific name
 * like "Banana" is preferred over a generic one, since a specific name is what
 * the nutrition lookup needs.</p>
 */
@Service
public class FoodRecognitionService {

    /**
     * Generic, low-specificity labels that Vision often returns alongside the
     * real item name. They're not discarded entirely (a generic label is still
     * better than nothing) but they're ranked below anything more specific.
     */
    private static final Set<String> GENERIC_LABELS = Set.of(
            "food", "produce", "natural foods", "plant", "ingredient", "whole food",
            "local food", "superfood", "diet food", "food group", "cuisine", "dish",
            "recipe", "vegetable", "fruit", "still life photography", "still life",
            "vegan nutrition", "leaf vegetable", "root vegetable", "staple food"
    );

    private static final int MAX_ALTERNATIVES = 4;

    private final GoogleVisionClient visionClient;
    private final VisionProperties visionProperties;

    public FoodRecognitionService(GoogleVisionClient visionClient, VisionProperties visionProperties) {
        this.visionClient = visionClient;
        this.visionProperties = visionProperties;
    }

    public RecognitionResponse identify(byte[] imageBytes) {
        if (!visionProperties.isConfigured()) {
            throw new VisionApiNotConfiguredException(
                    "Image recognition isn't set up yet. Add a Google Cloud Vision API key as the " +
                    "GOOGLE_VISION_API_KEY environment variable - see the README for setup steps. " +
                    "In the meantime, you can still search for an item by name.");
        }

        List<VisionLabel> labels = visionClient.detectLabels(imageBytes);

        List<VisionLabel> ranked = labels.stream()
                .sorted(Comparator
                        .comparing((VisionLabel l) -> isGeneric(l.description()))
                        .thenComparing(Comparator.comparingDouble(VisionLabel::score).reversed()))
                .toList();

        VisionLabel best = ranked.get(0);

        Set<String> alternatives = new LinkedHashSet<>();
        for (VisionLabel label : ranked) {
            if (alternatives.size() >= MAX_ALTERNATIVES) {
                break;
            }
            if (!label.description().equalsIgnoreCase(best.description())) {
                alternatives.add(label.description());
            }
        }

        return new RecognitionResponse(best.description(), best.score(), List.copyOf(alternatives));
    }

    private boolean isGeneric(String description) {
        return GENERIC_LABELS.contains(description.toLowerCase());
    }
}
