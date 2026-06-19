package com.nuvimi.foodrecognition.client;

import com.nuvimi.foodrecognition.config.VisionProperties;
import com.nuvimi.foodrecognition.exception.RecognitionException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Talks to the Google Cloud Vision {@code images:annotate} REST endpoint to
 * get label guesses for an uploaded image.
 *
 * <p>This is intentionally the only class in the service that knows about
 * Vision's request/response shape - everything else depends on the plain
 * {@link com.nuvimi.foodrecognition.model.RecognitionResponse} contract, so
 * swapping in a different provider later only means rewriting this class.</p>
 */
@Component
public class GoogleVisionClient {

    private final RestClient restClient;
    private final VisionProperties visionProperties;

    public GoogleVisionClient(RestClient visionRestClient, VisionProperties visionProperties) {
        this.restClient = visionRestClient;
        this.visionProperties = visionProperties;
    }

    /**
     * Calls the Vision API's LABEL_DETECTION feature and returns the raw
     * labels, ordered by descending confidence score.
     */
    public List<VisionLabel> detectLabels(byte[] imageBytes) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> requestBody = Map.of(
                "requests", List.of(
                        Map.of(
                                "image", Map.of("content", base64Image),
                                "features", List.of(
                                        Map.of("type", "LABEL_DETECTION", "maxResults", 15)
                                )
                        )
                )
        );

        String url = visionProperties.endpoint() + "?key=" + visionProperties.apiKey();

        try {
            VisionApiResponse response = restClient.post()
                    .uri(url)
                    .body(requestBody)
                    .retrieve()
                    .body(VisionApiResponse.class);

            if (response == null || response.responses() == null || response.responses().isEmpty()) {
                throw new RecognitionException("The vision service returned an empty response.");
            }

            VisionApiResponseItem firstResult = response.responses().get(0);

            if (firstResult.error() != null) {
                throw new RecognitionException("Vision API error: " + firstResult.error().message());
            }

            List<VisionLabel> labels = firstResult.labelAnnotations();
            if (labels == null || labels.isEmpty()) {
                throw new RecognitionException("No recognizable item was found in that image.");
            }
            return labels;
        } catch (RestClientException ex) {
            throw new RecognitionException("Could not reach the image recognition service.", ex);
        }
    }

    // ---- Wire-format DTOs for the Vision API response (field names match the JSON exactly) ----

    public record VisionApiResponse(List<VisionApiResponseItem> responses) {
    }

    public record VisionApiResponseItem(List<VisionLabel> labelAnnotations, VisionApiError error) {
    }

    public record VisionLabel(String description, double score) {
    }

    public record VisionApiError(int code, String message) {
    }
}
