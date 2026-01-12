package com.mallikarjun.insight_ai.model;

import lombok.Data;

import java.util.List;

@Data
public class AiModelResponse {
    private String ticker;
    private String impact;
    private Double impactScore; // Ensure this is spelled exactly like this
    private List<Summary> marketNoise;
    private List<Summary> fundamental;
    private String detailedReport;

    @Data
    public static class Summary {
        private String headline;
        private String newsSummary; // Changed from NewsSummary to camelCase
    }
}
