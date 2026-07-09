package com.mindmirror.service;

import com.mindmirror.client.AiServiceClient;
import com.mindmirror.client.dto.AiDtos.AiDailyEntry;
import com.mindmirror.client.dto.AiDtos.AiPredictResponse;
import com.mindmirror.client.dto.AiDtos.AiPredictions;
import com.mindmirror.client.dto.AiDtos.AiTrendsResponse;
import com.mindmirror.dto.response.DashboardResponse;
import com.mindmirror.dto.response.DashboardResponse.*;
import com.mindmirror.dto.response.PredictionResponse;
import com.mindmirror.dto.response.TrendInsightResponse;
import com.mindmirror.entity.DailyLog;
import com.mindmirror.entity.User;
import com.mindmirror.entity.UserStats;
import com.mindmirror.entity.WellnessScore;
import com.mindmirror.exception.BadRequestException;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.UserStatsRepository;
import com.mindmirror.repository.WellnessScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final int WINDOW_DAYS = 30;

    private final DailyLogRepository dailyLogRepository;
    private final WellnessScoreRepository wellnessScoreRepository;
    private final UserStatsRepository statsRepository;
    private final AiServiceClient aiClient;
    private final UserService userService;

    public DashboardService(DailyLogRepository dailyLogRepository,
                            WellnessScoreRepository wellnessScoreRepository,
                            UserStatsRepository statsRepository,
                            AiServiceClient aiClient, UserService userService) {
        this.dailyLogRepository = dailyLogRepository;
        this.wellnessScoreRepository = wellnessScoreRepository;
        this.statsRepository = statsRepository;
        this.aiClient = aiClient;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(WINDOW_DAYS - 1L);

        List<DailyLog> logs = dailyLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDate(userId, from, today);
        List<WellnessScore> scores = wellnessScoreRepository
                .findByUserIdAndLogDateBetweenOrderByLogDate(userId, from, today);
        Map<LocalDate, WellnessScore> scoreByDate = scores.stream()
                .collect(Collectors.toMap(WellnessScore::getLogDate, s -> s, (a, b) -> b));

        List<Point> moodTrend = logs.stream().map(l -> new Point(l.getLogDate(), l.getMoodScore())).toList();
        List<Point> sleepTrend = logs.stream().map(l -> new Point(l.getLogDate(), l.getSleepHours())).toList();
        List<Point> screenTime = logs.stream().map(l -> new Point(l.getLogDate(), l.getScreenTimeHours())).toList();

        List<HeatCell> heatmap = logs.stream().map(l -> {
            WellnessScore s = scoreByDate.get(l.getLogDate());
            double v = s != null ? s.getOverallWellnessScore() : l.getMoodScore() * 10.0;
            return new HeatCell(l.getLogDate(), round1(v), colorFor(v));
        }).toList();

        WellnessScore latest = scores.isEmpty() ? null : scores.get(scores.size() - 1);
        WellnessScore prev = scores.size() >= 2 ? scores.get(scores.size() - 2) : null;
        DailyLog latestLog = logs.isEmpty() ? null : logs.get(logs.size() - 1);
        DailyLog prevLog = logs.size() >= 2 ? logs.get(logs.size() - 2) : null;

        List<MetricCard> cards = buildCards(latest, prev, latestLog, prevLog);
        List<Dimension> radar = buildRadar(latest, latestLog);
        List<Slice> activity = buildActivityDistribution(latestLog);

        UserStats stats = statsRepository.findByUserId(userId).orElse(new UserStats(userId));
        long loggedDays = dailyLogRepository.countByUserId(userId);

        return new DashboardResponse(cards, moodTrend, sleepTrend, screenTime, radar, activity, heatmap,
                stats.getTotalXp(), stats.getLevel(), stats.getCurrentStreak(), (int) loggedDays);
    }

    @Transactional(readOnly = true)
    public List<TrendInsightResponse> trends(Long userId) {
        List<AiDailyEntry> history = historyEntries(userId);
        User user = userService.require(userId);
        AiTrendsResponse resp = aiClient.trends(history, user.getLocale());
        return resp.insights().stream()
                .map(i -> new TrendInsightResponse(i.kind(), i.textEn(), i.textMk())).toList();
    }

    @Transactional(readOnly = true)
    public PredictionResponse prediction(Long userId) {
        List<AiDailyEntry> history = historyEntries(userId);
        if (history.isEmpty()) {
            throw new BadRequestException("Log at least one day before requesting a prediction.");
        }
        User user = userService.require(userId);
        AiPredictResponse pred = aiClient.predict(history, user.getLocale());
        AiPredictions p = pred.predictions();
        String en = p.recommendedActivityText() == null ? null : p.recommendedActivityText().textEn();
        String mk = p.recommendedActivityText() == null ? null : p.recommendedActivityText().textMk();
        return new PredictionResponse(
                LocalDate.now().plusDays(1), p.moodTomorrow(), p.burnoutTomorrow(), p.stressTomorrow(),
                p.sleepQualityTomorrow(), p.recommendedActivity(), en, mk, pred.modelVersion(),
                pred.featureImportance());
    }

    // --- helpers ------------------------------------------------------------

    private List<AiDailyEntry> historyEntries(Long userId) {
        LocalDate today = LocalDate.now();
        List<DailyLog> logs = dailyLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDate(userId, today.minusDays(WINDOW_DAYS), today);
        return AiServiceClient.toEntries(logs);
    }

    private List<MetricCard> buildCards(WellnessScore latest, WellnessScore prev,
                                        DailyLog latestLog, DailyLog prevLog) {
        List<MetricCard> cards = new ArrayList<>();
        if (latest != null) {
            cards.add(new MetricCard("overall_wellness", "Overall Wellness", "Целокупна благосостојба",
                    round1(latest.getOverallWellnessScore()), "/100",
                    prev == null ? null : round1(latest.getOverallWellnessScore() - prev.getOverallWellnessScore())));
            cards.add(new MetricCard("sleep", "Sleep", "Спиење",
                    round1(latest.getSleepScore()), "/100",
                    prev == null ? null : round1(latest.getSleepScore() - prev.getSleepScore())));
            cards.add(new MetricCard("burnout", "Burnout", "Исцрпеност",
                    round1(latest.getBurnoutIndex()), "/100",
                    prev == null ? null : round1(latest.getBurnoutIndex() - prev.getBurnoutIndex())));
            cards.add(new MetricCard("social", "Social Score", "Социјален индекс",
                    round1(latest.getSocialBalanceScore()), "/100",
                    prev == null ? null : round1(latest.getSocialBalanceScore() - prev.getSocialBalanceScore())));
        }
        if (latestLog != null) {
            cards.add(new MetricCard("mood", "Mood", "Расположение",
                    latestLog.getMoodScore(), "/10",
                    prevLog == null ? null : (double) (latestLog.getMoodScore() - prevLog.getMoodScore())));
            cards.add(new MetricCard("stress", "Stress", "Стрес",
                    latestLog.getStressLevel(), "/10",
                    prevLog == null ? null : (double) (latestLog.getStressLevel() - prevLog.getStressLevel())));
            cards.add(new MetricCard("activity", "Physical Activity", "Физичка активност",
                    latestLog.getPhysicalActivityMin(), "min",
                    prevLog == null ? null : (double) (latestLog.getPhysicalActivityMin() - prevLog.getPhysicalActivityMin())));
        }
        return cards;
    }

    private List<Dimension> buildRadar(WellnessScore latest, DailyLog latestLog) {
        if (latest == null) return List.of();
        return List.of(
                new Dimension("sleep", "Sleep", "Спиење", round1(latest.getSleepScore())),
                new Dimension("wellbeing", "Wellbeing", "Благосостојба", round1(latest.getWellbeingScore())),
                new Dimension("social", "Social", "Социјализација", round1(latest.getSocialBalanceScore())),
                new Dimension("productivity", "Productivity", "Продуктивност", round1(latest.getProductivityScore())),
                new Dimension("resilience", "Low Burnout", "Ниска исцрпеност", round1(100 - latest.getBurnoutIndex())),
                new Dimension("mood", "Mood", "Расположение", latestLog == null ? 0 : latestLog.getMoodScore() * 10.0)
        );
    }

    private List<Slice> buildActivityDistribution(DailyLog l) {
        if (l == null) return List.of();
        double sleep = l.getSleepHours();
        double study = l.getStudyHours();
        double screen = l.getScreenTimeHours();
        double activity = l.getPhysicalActivityMin() / 60.0;
        double social = l.getSocialTimeMin() / 60.0;
        double other = Math.max(0, 24 - (sleep + study + screen + activity + social));
        return List.of(
                new Slice("sleep", "Sleep", "Спиење", round1(sleep)),
                new Slice("study", "Study", "Учење", round1(study)),
                new Slice("screen", "Screen", "Екран", round1(screen)),
                new Slice("activity", "Activity", "Активност", round1(activity)),
                new Slice("social", "Social", "Дружење", round1(social)),
                new Slice("other", "Other", "Друго", round1(other))
        );
    }

    private static String colorFor(double overall) {
        if (overall >= 80) return "green";
        if (overall >= 60) return "yellow";
        if (overall >= 40) return "orange";
        return "red";
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
