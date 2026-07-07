package com.mindmirror.controller;

import com.mindmirror.dto.response.RecommendationResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    @Operation(summary = "Latest personalised recommendations (bilingual)")
    public List<RecommendationResponse> latest(@AuthenticationPrincipal UserPrincipal principal,
                                               @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.latest(principal.getId(), limit);
    }
}
