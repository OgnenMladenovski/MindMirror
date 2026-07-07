package com.mindmirror.controller;

import com.mindmirror.dto.response.AchievementResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@Tag(name = "Achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    @Operation(summary = "Achievement catalog with the user's unlock progress")
    public List<AchievementResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return achievementService.list(principal.getId());
    }
}
