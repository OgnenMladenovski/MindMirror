package com.mindmirror.dto.response;

import com.mindmirror.entity.Recommendation;

import java.time.Instant;
import java.time.LocalDate;

public record RecommendationResponse(
        Long id,
        String category,
        String severity,
        String textEn,
        String textMk,
        LocalDate logDate,
        Instant createdAt
) {
    public static RecommendationResponse from(Recommendation r) {
        return new RecommendationResponse(
                r.getId(), r.getCategory(), r.getSeverity(), r.getTextEn(), r.getTextMk(),
                r.getLogDate(), r.getCreatedAt());
    }
}
