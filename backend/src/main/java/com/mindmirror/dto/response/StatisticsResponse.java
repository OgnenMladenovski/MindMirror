package com.mindmirror.dto.response;

import java.util.List;
import java.util.Map;

/** Admin analytics across all users (anonymous, aggregated). */
public record StatisticsResponse(
        long totalUsers,
        long totalLogs,
        double averageWellness,
        Map<String, Long> burnoutDistribution,     // Low/Medium/High -> count
        List<ChallengeStat> mostCommonChallenges,
        List<HbscComparisonResponse.Row> hbscComparison
) {
    public record ChallengeStat(String type, long completedCount) { }
}
