package com.mindmirror.dto.response;

import com.mindmirror.entity.AvatarState;

import java.time.LocalDate;

public record AvatarResponse(
        String state,
        String animation,
        String attributesJson,
        String captionEn,
        String captionMk,
        LocalDate logDate
) {
    public static AvatarResponse from(AvatarState a) {
        return new AvatarResponse(
                a.getState(), a.getAnimation(), a.getAttributesJson(),
                a.getCaptionEn(), a.getCaptionMk(), a.getLogDate());
    }
}
