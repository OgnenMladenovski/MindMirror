package com.mindmirror.dto.response;

import java.time.LocalDate;
import java.util.List;

/** Everything the analytics dashboard needs: summary cards + chart series. */
public record DashboardResponse(
        List<MetricCard> cards,
        List<Point> moodTrend,       // line chart — last 30 days mood
        List<Point> sleepTrend,      // area chart — sleep hours
        List<Point> screenTime,      // bar chart — screen time hours
        List<Dimension> wellnessRadar,   // radar chart — wellness dimensions
        List<Slice> activityDistribution, // pie chart — how the day is spent
        List<HeatCell> moodHeatmap,  // calendar heatmap — mood history
        int totalXp,
        int level,
        int currentStreak,
        int loggedDays
) {
    public record MetricCard(String key, String labelEn, String labelMk,
                             double value, String unit, Double delta) { }

    public record Point(LocalDate date, double value) { }

    public record Dimension(String key, String labelEn, String labelMk, double value) { }

    public record Slice(String key, String labelEn, String labelMk, double value) { }

    public record HeatCell(LocalDate date, double value, String color) { }
}
