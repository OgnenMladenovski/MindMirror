package com.mindmirror.dto.response;

public record ChatMessageResponse(
        String reply,
        String replyEn,
        String replyMk,
        String intent,
        String backend
) { }
