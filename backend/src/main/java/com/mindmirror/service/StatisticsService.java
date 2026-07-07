package com.mindmirror.service;

import com.mindmirror.dto.response.HbscComparisonResponse.Row;
import com.mindmirror.dto.response.StatisticsResponse;
import com.mindmirror.dto.response.StatisticsResponse.ChallengeStat;
import com.mindmirror.repository.ChallengeRepository;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.UserRepository;
import com.mindmirror.repository.WellnessScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregated, anonymous analytics for the admin dashboard. */
@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final DailyLogRepository dailyLogRepository;
    private final WellnessScoreRepository wellnessScoreRepository;
    private final ChallengeRepository challengeRepository;
    private final HbscService hbscService;

    public StatisticsService(UserRepository userRepository, DailyLogRepository dailyLogRepository,
                             WellnessScoreRepository wellnessScoreRepository,
                             ChallengeRepository challengeRepository, HbscService hbscService) {
        this.userRepository = userRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.wellnessScoreRepository = wellnessScoreRepository;
        this.challengeRepository = challengeRepository;
        this.hbscService = hbscService;
    }

    @Transactional(readOnly = true)
    public StatisticsResponse overview() {
        long totalUsers = userRepository.count();
        long totalLogs = dailyLogRepository.count();

        Double avgWellness = wellnessScoreRepository.averageOverallWellness();
        double averageWellness = avgWellness == null ? 0 : Math.round(avgWellness * 10.0) / 10.0;

        Map<String, Long> burnoutDistribution = new LinkedHashMap<>();
        burnoutDistribution.put("Low", 0L);
        burnoutDistribution.put("Medium", 0L);
        burnoutDistribution.put("High", 0L);
        for (Object[] row : wellnessScoreRepository.countByRiskLevel()) {
            burnoutDistribution.put((String) row[0], (Long) row[1]);
        }

        List<ChallengeStat> challenges = challengeRepository.countCompletedByType().stream()
                .map(r -> new ChallengeStat((String) r[0], (Long) r[1])).toList();

        List<Row> hbsc = globalHbscComparison();

        return new StatisticsResponse(totalUsers, totalLogs, averageWellness,
                burnoutDistribution, challenges, hbsc);
    }

    private List<Row> globalHbscComparison() {
        List<Object[]> agg = dailyLogRepository.globalAverages();
        if (agg.isEmpty() || agg.get(0)[0] == null) {
            return hbscService.buildComparison(null, null, null, null, null, 15);
        }
        Object[] a = agg.get(0);
        return hbscService.buildComparison(
                toDouble(a[0]), toDouble(a[2]), toDouble(a[1]), toDouble(a[3]), toDouble(a[4]), 15);
    }

    private static Double toDouble(Object o) {
        return o == null ? null : ((Number) o).doubleValue();
    }
}
