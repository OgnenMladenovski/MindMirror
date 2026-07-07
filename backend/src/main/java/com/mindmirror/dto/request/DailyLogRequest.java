package com.mindmirror.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** One daily wellness check-in. {@code logDate} defaults to today when omitted. */
public record DailyLogRequest(
        LocalDate logDate,

        @DecimalMin("0.0") @DecimalMax("24.0") double sleepHours,
        @Min(1) @Max(10) int stressLevel,
        @Min(1) @Max(10) int moodScore,
        @Size(max = 8) String moodEmoji,
        @Min(0) @Max(1440) int physicalActivityMin,
        @DecimalMin("0.0") @DecimalMax("10.0") double waterIntake,
        @DecimalMin("0.0") @DecimalMax("24.0") double screenTimeHours,
        @DecimalMin("0.0") @DecimalMax("24.0") double studyHours,
        @Min(0) @Max(1440) int socialTimeMin,
        @Min(1) @Max(10) int energyLevel,
        @Min(1) @Max(10) int nutritionQuality,
        @Size(max = 1000) String notes
) { }
