package com.mindmirror.dto.response;

import com.mindmirror.entity.PredictionHistory;

import java.time.LocalDate;
import java.util.Map;

public record PredictionResponse(
        LocalDate forDate,
        double moodTomorrow,
        double burnoutTomorrow,
        double stressTomorrow,
        double sleepQualityTomorrow,
        String recommendedActivity,
        String recommendedActivityEn,
        String recommendedActivityMk,
        String modelVersion,
        Map<String, Map<String, Double>> featureImportance
) {
    public static PredictionResponse from(PredictionHistory p, String actEn, String actMk,
                                          Map<String, Map<String, Double>> importance) {
        return new PredictionResponse(
                p.getForDate(), p.getMoodTomorrow(), p.getBurnoutTomorrow(),
                p.getStressTomorrow(), p.getSleepQualityTomorrow(), p.getRecommendedActivity(),
                actEn, actMk, p.getModelVersion(), importance);
    }
}
