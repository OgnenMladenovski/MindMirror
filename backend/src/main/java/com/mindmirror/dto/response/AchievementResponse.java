package com.mindmirror.dto.response;

import com.mindmirror.entity.Achievement;

import java.time.Instant;

public record AchievementResponse(
        Long id,
        String code,
        String titleEn,
        String titleMk,
        String descriptionEn,
        String descriptionMk,
        String icon,
        int xpReward,
        boolean unlocked,
        int progress,
        Instant unlockedAt
) {
    public static AchievementResponse of(Achievement a, boolean unlocked, int progress, Instant unlockedAt) {
        return new AchievementResponse(
                a.getId(), a.getCode(), a.getTitleEn(), a.getTitleMk(),
                a.getDescriptionEn(), a.getDescriptionMk(), a.getIcon(), a.getXpReward(),
                unlocked, progress, unlockedAt);
    }
}
