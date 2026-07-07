package com.mindmirror.controller;

import com.mindmirror.dto.request.ChatRequestDto;
import com.mindmirror.dto.response.ChatMessageResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "AI Chat Assistant")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Ask the wellbeing assistant a question grounded in your history")
    public ChatMessageResponse chat(@AuthenticationPrincipal UserPrincipal principal,
                                    @Valid @RequestBody ChatRequestDto request) {
        return chatService.chat(principal.getId(), request.message(), request.lang());
    }
}
