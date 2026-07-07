package com.mindmirror.dto.response;

import com.mindmirror.entity.DailyLog;

import java.time.LocalDate;
import java.util.List;

/** Full result of a daily check-in: the saved log plus AI-derived outputs. */
public record DailyLogResponse(
        Long id,
        LocalDate logDate,
        double sleepHours,
        int stressLevel,
        int moodScore,
        String moodEmoji,
        int physicalActivityMin,
        double waterIntake,
        double screenTimeHours,
        double studyHours,
        int socialTimeMin,
        int energyLevel,
        int nutritionQuality,
        String notes,
        ScoreResponse scores,
        AvatarResponse avatar,
        List<RecommendationResponse> recommendations,
        PredictionResponse prediction
) {
    public static DailyLogResponse of(DailyLog l, ScoreResponse scores, AvatarResponse avatar,
                                      List<RecommendationResponse> recs, PredictionResponse prediction) {
        return new DailyLogResponse(
                l.getId(), l.getLogDate(), l.getSleepHours(), l.getStressLevel(), l.getMoodScore(),
                l.getMoodEmoji(), l.getPhysicalActivityMin(), l.getWaterIntake(), l.getScreenTimeHours(),
                l.getStudyHours(), l.getSocialTimeMin(), l.getEnergyLevel(), l.getNutritionQuality(),
                l.getNotes(), scores, avatar, recs, prediction);
    }
}
