package com.mindmirror.controller;

import com.mindmirror.dto.response.DashboardResponse;
import com.mindmirror.dto.response.PredictionResponse;
import com.mindmirror.dto.response.TrendInsightResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard & Analytics")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Summary cards + all chart series for the analytics dashboard")
    public DashboardResponse dashboard(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardService.dashboard(principal.getId());
    }

    @GetMapping("/trends")
    @Operation(summary = "Automatically generated trend insights (bilingual)")
    public List<TrendInsightResponse> trends(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardService.trends(principal.getId());
    }

    @GetMapping("/prediction")
    @Operation(summary = "ML prediction for tomorrow + feature importance")
    public PredictionResponse prediction(@AuthenticationPrincipal UserPrincipal principal) {
        return dashboardService.prediction(principal.getId());
    }
}
