package com.mindmirror.controller;

import com.mindmirror.dto.response.ChallengeResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/today")
    @Operation(summary = "Get (and generate if needed) today's challenge")
    public ChallengeResponse today(@AuthenticationPrincipal UserPrincipal principal) {
        return challengeService.today(principal.getId());
    }

    @GetMapping
    @Operation(summary = "List the user's challenges")
    public List<ChallengeResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return challengeService.list(principal.getId());
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a challenge and earn XP")
    public ChallengeResponse complete(@AuthenticationPrincipal UserPrincipal principal,
                                      @PathVariable Long id) {
        return challengeService.complete(principal.getId(), id);
    }
}
