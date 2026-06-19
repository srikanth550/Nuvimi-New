package com.nuvimi.nutrition.client;

import com.nuvimi.nutrition.config.UsdaProperties;
import com.nuvimi.nutrition.exception.FoodNotFoundException;
import com.nuvimi.nutrition.exception.NutritionLookupException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Talks to the USDA FoodData Central {@code /foods/search} REST endpoint.
 *
 * <p>This is the only class that knows the shape of USDA's JSON - everything
 * downstream of this works with the plain {@link UsdaFood} record.</p>
 */
@Component
public class UsdaFoodDataClient {

    private final RestClient restClient;
    private final UsdaProperties usdaProperties;

    public UsdaFoodDataClient(RestClient usdaRestClient, UsdaProperties usdaProperties) {
        this.restClient = usdaRestClient;
        this.usdaProperties = usdaProperties;
    }

    /**
     * Searches FoodData Central and returns the single best-matching food,
     * preferring the well-curated Foundation and SR Legacy data types (whole,
     * unbranded foods) since this app is aimed at fruits, vegetables and
     * plain ingredients rather than packaged products.
     */
    public UsdaFood searchBestMatch(String query) {
        try {
            UsdaSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/foods/search")
                            .queryParam("query", query)
                            .queryParam("pageSize", 5)
                            .queryParam("dataType", "Foundation,SR Legacy")
                            .queryParam("api_key", usdaProperties.apiKey())
                            .build())
                    .retrieve()
                    .body(UsdaSearchResponse.class);

            if (response == null || response.foods() == null || response.foods().isEmpty()) {
                throw new FoodNotFoundException(query);
            }

            return response.foods().get(0);
        } catch (FoodNotFoundException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new NutritionLookupException(
                    "Could not reach the USDA FoodData Central service. If you're using the default " +
                    "DEMO_KEY, it may have hit its 30 requests/hour limit - see the README for how to " +
                    "use your own free API key.", ex);
        }
    }

    // ---- Wire-format DTOs for the USDA response (field names match the JSON exactly) ----

    public record UsdaSearchResponse(List<UsdaFood> foods) {
    }

    public record UsdaFood(long fdcId, String description, String dataType, List<UsdaNutrient> foodNutrients) {
    }

    public record UsdaNutrient(int nutrientId, String nutrientName, String unitName, Double value) {
    }
}
