package com.mindmirror.controller;

import com.mindmirror.dto.response.AvatarResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/avatar")
@Tag(name = "Digital Twin Avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    @Operation(summary = "Current avatar state (reflects the latest check-in)")
    public AvatarResponse current(@AuthenticationPrincipal UserPrincipal principal) {
        return avatarService.current(principal.getId());
    }

    @GetMapping("/history")
    @Operation(summary = "Avatar state history")
    public List<AvatarResponse> history(@AuthenticationPrincipal UserPrincipal principal) {
        return avatarService.history(principal.getId());
    }
}
