package com.mindmirror.controller;

import com.mindmirror.dto.request.DailyLogRequest;
import com.mindmirror.dto.response.DailyLogResponse;
import com.mindmirror.security.UserPrincipal;
import com.mindmirror.service.DailyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Daily Logs")
public class DailyLogController {

    private final DailyLogService dailyLogService;

    public DailyLogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }

    @PostMapping
    @Operation(summary = "Submit a daily check-in (runs AI analysis, recommendations, avatar, prediction)")
    public ResponseEntity<DailyLogResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody DailyLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dailyLogService.create(principal.getId(), request));
    }

    @GetMapping
    @Operation(summary = "List the current user's daily logs (newest first)")
    public List<DailyLogResponse> history(@AuthenticationPrincipal UserPrincipal principal) {
        return dailyLogService.history(principal.getId());
    }

    @GetMapping("/{date}")
    @Operation(summary = "Get a single day's log with its stored analysis")
    public DailyLogResponse byDate(@AuthenticationPrincipal UserPrincipal principal,
                                   @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dailyLogService.getByDate(principal.getId(), date);
    }
}
