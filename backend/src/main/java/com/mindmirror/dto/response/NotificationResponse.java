package com.mindmirror.dto.response;

import com.mindmirror.entity.Notification;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String type,
        String titleEn,
        String titleMk,
        String bodyEn,
        String bodyMk,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getType().name(), n.getTitleEn(), n.getTitleMk(),
                n.getBodyEn(), n.getBodyMk(), n.isReadFlag(), n.getCreatedAt());
    }
}
