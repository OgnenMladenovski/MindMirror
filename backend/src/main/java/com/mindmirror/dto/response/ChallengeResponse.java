package com.mindmirror.dto.response;

import com.mindmirror.entity.Challenge;

import java.time.Instant;
import java.time.LocalDate;

public record ChallengeResponse(
        Long id,
        String type,
        String titleEn,
        String titleMk,
        String descriptionEn,
        String descriptionMk,
        int xpReward,
        String status,
        LocalDate challengeDate,
        Instant completedAt
) {
    public static ChallengeResponse from(Challenge c) {
        return new ChallengeResponse(
                c.getId(), c.getType(), c.getTitleEn(), c.getTitleMk(),
                c.getDescriptionEn(), c.getDescriptionMk(), c.getXpReward(),
                c.getStatus().name(), c.getChallengeDate(), c.getCompletedAt());
    }
}
