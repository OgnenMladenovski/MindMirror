package com.mindmirror.dto.response;

import com.mindmirror.entity.WellnessScore;

import java.time.LocalDate;

public record ScoreResponse(
        LocalDate logDate,
        double burnoutIndex,
        double sleepScore,
        double wellbeingScore,
        double socialBalanceScore,
        double productivityScore,
        double overallWellnessScore,
        String riskLevel
) {
    public static ScoreResponse from(WellnessScore w) {
        return new ScoreResponse(
                w.getLogDate(), w.getBurnoutIndex(), w.getSleepScore(), w.getWellbeingScore(),
                w.getSocialBalanceScore(), w.getProductivityScore(), w.getOverallWellnessScore(),
                w.getRiskLevel());
    }
}
