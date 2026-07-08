package com.mindmirror.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTOs for talking to the FastAPI AI microservice. Field JSON names are
 * snake_case to match the pydantic schemas; the rest of the app stays camelCase.
 */
public final class AiDtos {

    private AiDtos() { }

    public record AiDailyEntry(
            @JsonProperty("log_date") String logDate,
            @JsonProperty("sleep_hours") double sleepHours,
            @JsonProperty("stress_level") int stressLevel,
            @JsonProperty("mood_score") int moodScore,
            @JsonProperty("physical_activity_min") int physicalActivityMin,
            @JsonProperty("water_intake") double waterIntake,
            @JsonProperty("screen_time_hours") double screenTimeHours,
            @JsonProperty("study_hours") double studyHours,
            @JsonProperty("social_time_min") int socialTimeMin,
            @JsonProperty("energy_level") int energyLevel,
            @JsonProperty("nutrition_quality") int nutritionQuality,
            @JsonProperty("notes") String notes
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiScores(
            @JsonProperty("burnout_index") double burnoutIndex,
            @JsonProperty("sleep_score") double sleepScore,
            @JsonProperty("wellbeing_score") double wellbeingScore,
            @JsonProperty("social_balance_score") double socialBalanceScore,
            @JsonProperty("productivity_score") double productivityScore,
            @JsonProperty("overall_wellness_score") double overallWellnessScore,
            @JsonProperty("risk_level") String riskLevel
    ) { }

    public record AiAnalyzeRequest(
            AiDailyEntry entry,
            List<AiDailyEntry> history,
            @JsonProperty("age_group") int ageGroup,
            String lang
    ) { }

    public record AiHistoryRequest(List<AiDailyEntry> history, String lang) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiRecommendation(
            String category,
            String severity,
            @JsonProperty("text_en") String textEn,
            @JsonProperty("text_mk") String textMk
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiRecommendResponse(List<AiRecommendation> recommendations) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiInsight(
            String kind,
            @JsonProperty("text_en") String textEn,
            @JsonProperty("text_mk") String textMk
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiTrendsResponse(List<AiInsight> insights) { }

    public record AiAvatarRequest(
            AiScores scores,
            AiDailyEntry entry,
            List<AiDailyEntry> history,
            String lang
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiAvatarResponse(
            String state,
            String animation,
            Map<String, Object> attributes,
            @JsonProperty("caption_en") String captionEn,
            @JsonProperty("caption_mk") String captionMk
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiLocalizedText(
            @JsonProperty("text_en") String textEn,
            @JsonProperty("text_mk") String textMk
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiPredictions(
            @JsonProperty("mood_tomorrow") double moodTomorrow,
            @JsonProperty("burnout_tomorrow") double burnoutTomorrow,
            @JsonProperty("stress_tomorrow") double stressTomorrow,
            @JsonProperty("sleep_quality_tomorrow") double sleepQualityTomorrow,
            @JsonProperty("recommended_activity") String recommendedActivity,
            @JsonProperty("recommended_activity_text") AiLocalizedText recommendedActivityText
    ) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AiPredictResponse(
            AiPredictions predictions,
            @JsonProperty("model_version") String modelVersion,
            @JsonProperty("feature_importance") Map<String, Map<String, Double>> featureImportance
    ) { }
}
