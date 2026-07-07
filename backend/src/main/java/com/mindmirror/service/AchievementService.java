package com.mindmirror.service;

import com.mindmirror.dto.response.AchievementResponse;
import com.mindmirror.entity.Achievement;
import com.mindmirror.entity.DailyLog;
import com.mindmirror.entity.UserAchievement;
import com.mindmirror.entity.WellnessScore;
import com.mindmirror.entity.enums.NotificationType;
import com.mindmirror.repository.AchievementRepository;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.UserAchievementRepository;
import com.mindmirror.repository.WellnessScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AchievementService {

    /** code -> threshold count needed to unlock. */
    private static final Map<String, Integer> THRESHOLDS = Map.of(
            "SEVEN_HEALTHY_DAYS", 7,
            "EARLY_SLEEPER", 5,
            "HYDRATION_MASTER", 5,
            "STRESS_FIGHTER", 5,
            "FITNESS_HERO", 5,
            "MOOD_EXPLORER", 10
    );

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final DailyLogRepository dailyLogRepository;
    private final WellnessScoreRepository wellnessScoreRepository;
    private final UserStatsService statsService;
    private final NotificationService notificationService;

    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              DailyLogRepository dailyLogRepository,
                              WellnessScoreRepository wellnessScoreRepository,
                              UserStatsService statsService,
                              NotificationService notificationService) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.dailyLogRepository = dailyLogRepository;
        this.wellnessScoreRepository = wellnessScoreRepository;
        this.statsService = statsService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void evaluate(Long userId) {
        List<DailyLog> logs = dailyLogRepository.findByUserIdOrderByLogDateDesc(userId);
        List<WellnessScore> scores = wellnessScoreRepository.findByUserIdOrderByLogDateDesc(userId);

        Map<String, Long> current = Map.of(
                "SEVEN_HEALTHY_DAYS", scores.stream().filter(s -> s.getOverallWellnessScore() >= 70).count(),
                "EARLY_SLEEPER", logs.stream().filter(l -> l.getSleepHours() >= 8).count(),
                "HYDRATION_MASTER", logs.stream().filter(l -> l.getWaterIntake() >= 2.0).count(),
                "STRESS_FIGHTER", logs.stream().filter(l -> l.getStressLevel() <= 4).count(),
                "FITNESS_HERO", logs.stream().filter(l -> l.getPhysicalActivityMin() >= 60).count(),
                "MOOD_EXPLORER", (long) logs.size()
        );

        current.forEach((code, count) -> {
            Achievement achievement = achievementRepository.findByCode(code).orElse(null);
            if (achievement == null) return;
            int threshold = THRESHOLDS.getOrDefault(code, 1);
            int progress = (int) Math.min(100, Math.round(100.0 * count / threshold));

            UserAchievement ua = userAchievementRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElseGet(() -> new UserAchievement(userId, achievement.getId()));

            boolean wasUnlocked = ua.getUnlockedAt() != null;
            ua.setProgress(progress);
            if (!wasUnlocked && count >= threshold) {
                ua.setUnlockedAt(Instant.now());
                userAchievementRepository.save(ua);
                statsService.addXp(userId, achievement.getXpReward());
                notificationService.create(userId, NotificationType.ACHIEVEMENT,
                        "Achievement unlocked: " + achievement.getTitleEn(),
                        "Отклучено достигнување: " + achievement.getTitleMk(),
                        achievement.getDescriptionEn(), achievement.getDescriptionMk());
            } else {
                userAchievementRepository.save(ua);
            }
        });
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> list(Long userId) {
        List<UserAchievement> mine = userAchievementRepository.findByUserId(userId);
        return achievementRepository.findAll().stream().map(a -> {
            UserAchievement ua = mine.stream()
                    .filter(x -> x.getAchievementId().equals(a.getId()))
                    .findFirst().orElse(null);
            boolean unlocked = ua != null && ua.getUnlockedAt() != null;
            int progress = ua == null ? 0 : ua.getProgress();
            Instant when = ua == null ? null : ua.getUnlockedAt();
            return AchievementResponse.of(a, unlocked, progress, when);
        }).toList();
    }
}
