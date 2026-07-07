package com.mindmirror.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindmirror.client.AiServiceClient;
import com.mindmirror.client.AiServiceException;
import com.mindmirror.client.dto.AiDtos.*;
import com.mindmirror.dto.request.DailyLogRequest;
import com.mindmirror.dto.response.*;
import com.mindmirror.entity.*;
import com.mindmirror.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Orchestrates a daily check-in: persists the log, then calls the AI service to
 * analyse, recommend, derive the avatar and predict tomorrow — storing each result.
 */
@Service
public class DailyLogService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyLogService.class);
    private static final int HISTORY_DAYS = 30;

    private final DailyLogRepository dailyLogRepository;
    private final WellnessScoreRepository wellnessScoreRepository;
    private final RecommendationRepository recommendationRepository;
    private final AvatarStateRepository avatarStateRepository;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final AiServiceClient aiClient;
    private final UserService userService;
    private final UserStatsService statsService;
    private final ChallengeService challengeService;
    private final AchievementService achievementService;
    private final ObjectMapper objectMapper;

    public DailyLogService(DailyLogRepository dailyLogRepository,
                           WellnessScoreRepository wellnessScoreRepository,
                           RecommendationRepository recommendationRepository,
                           AvatarStateRepository avatarStateRepository,
                           PredictionHistoryRepository predictionHistoryRepository,
                           AiServiceClient aiClient, UserService userService,
                           UserStatsService statsService, ChallengeService challengeService,
                           AchievementService achievementService, ObjectMapper objectMapper) {
        this.dailyLogRepository = dailyLogRepository;
        this.wellnessScoreRepository = wellnessScoreRepository;
        this.recommendationRepository = recommendationRepository;
        this.avatarStateRepository = avatarStateRepository;
        this.predictionHistoryRepository = predictionHistoryRepository;
        this.aiClient = aiClient;
        this.userService = userService;
        this.statsService = statsService;
        this.challengeService = challengeService;
        this.achievementService = achievementService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DailyLogResponse create(Long userId, DailyLogRequest req) {
        User user = userService.require(userId);
        LocalDate date = req.logDate() != null ? req.logDate() : LocalDate.now();
        String lang = user.getLocale();

        DailyLog dailyLog = dailyLogRepository.findByUserIdAndLogDate(userId, date).orElseGet(DailyLog::new);
        dailyLog.setUserId(userId);
        dailyLog.setLogDate(date);
        apply(dailyLog, req);
        dailyLog = dailyLogRepository.save(dailyLog);

        List<DailyLog> fullHistory = dailyLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDate(userId, date.minusDays(HISTORY_DAYS), date);
        List<AiDailyEntry> aiFull = AiServiceClient.toEntries(fullHistory);
        List<AiDailyEntry> aiPrior = aiFull.size() > 1 ? aiFull.subList(0, aiFull.size() - 1) : List.of();
        AiDailyEntry aiEntry = AiServiceClient.toEntry(dailyLog);

        ScoreResponse scoreResp = null;
        AvatarResponse avatarResp = null;
        List<RecommendationResponse> recResps = List.of();
        PredictionResponse predResp = null;

        AiScores scores = null;
        try {
            scores = aiClient.analyze(new AiAnalyzeRequest(aiEntry, aiPrior, user.getAgeGroup(), lang));
            scoreResp = ScoreResponse.from(saveScore(dailyLog, userId, date, scores));
        } catch (AiServiceException e) {
            LOG.warn("analyze failed for user {} on {}: {}", userId, date, e.getMessage());
        }

        if (scores != null) {
            try {
                AiAvatarResponse av = aiClient.avatarState(new AiAvatarRequest(scores, aiEntry, List.of(), lang));
                avatarResp = AvatarResponse.from(saveAvatar(userId, date, av));
            } catch (AiServiceException e) {
                LOG.warn("avatar-state failed for user {}: {}", userId, e.getMessage());
            }
        }

        try {
            AiRecommendResponse rec = aiClient.recommend(aiFull, lang);
            recResps = saveRecommendations(dailyLog, userId, date, rec).stream()
                    .map(RecommendationResponse::from).toList();
        } catch (AiServiceException e) {
            LOG.warn("recommend failed for user {}: {}", userId, e.getMessage());
        }

        try {
            AiPredictResponse pred = aiClient.predict(aiFull, lang);
            predResp = savePrediction(userId, date, pred);
        } catch (AiServiceException e) {
            LOG.warn("predict failed for user {}: {}", userId, e.getMessage());
        }

        statsService.recordLog(userId, date);
        challengeService.ensureDailyChallenge(userId, date);
        achievementService.evaluate(userId);

        return DailyLogResponse.of(dailyLog, scoreResp, avatarResp, recResps, predResp);
    }

    @Transactional(readOnly = true)
    public List<DailyLogResponse> history(Long userId) {
        return dailyLogRepository.findByUserIdOrderByLogDateDesc(userId).stream()
                .map(this::assemble).toList();
    }

    @Transactional(readOnly = true)
    public DailyLogResponse getByDate(Long userId, LocalDate date) {
        DailyLog log = dailyLogRepository.findByUserIdAndLogDate(userId, date)
                .orElseThrow(() -> new com.mindmirror.exception.NotFoundException("No log for " + date));
        return assemble(log);
    }

    // --- helpers ------------------------------------------------------------

    private DailyLogResponse assemble(DailyLog dailyLog) {
        ScoreResponse score = wellnessScoreRepository.findByDailyLogId(dailyLog.getId())
                .map(ScoreResponse::from).orElse(null);
        AvatarResponse avatar = avatarStateRepository
                .findFirstByUserIdAndLogDateOrderByCreatedAtDesc(dailyLog.getUserId(), dailyLog.getLogDate())
                .map(AvatarResponse::from).orElse(null);
        List<RecommendationResponse> recs = recommendationRepository.findByDailyLogId(dailyLog.getId()).stream()
                .map(RecommendationResponse::from).toList();
        return DailyLogResponse.of(dailyLog, score, avatar, recs, null);
    }

    private void apply(DailyLog l, DailyLogRequest req) {
        l.setSleepHours(req.sleepHours());
        l.setStressLevel(req.stressLevel());
        l.setMoodScore(req.moodScore());
        l.setMoodEmoji(req.moodEmoji());
        l.setPhysicalActivityMin(req.physicalActivityMin());
        l.setWaterIntake(req.waterIntake());
        l.setScreenTimeHours(req.screenTimeHours());
        l.setStudyHours(req.studyHours());
        l.setSocialTimeMin(req.socialTimeMin());
        l.setEnergyLevel(req.energyLevel());
        l.setNutritionQuality(req.nutritionQuality());
        l.setNotes(req.notes());
    }

    private WellnessScore saveScore(DailyLog dailyLog, Long userId, LocalDate date, AiScores s) {
        WellnessScore ws = wellnessScoreRepository.findByDailyLogId(dailyLog.getId())
                .orElseGet(WellnessScore::new);
        ws.setDailyLogId(dailyLog.getId());
        ws.setUserId(userId);
        ws.setLogDate(date);
        ws.setBurnoutIndex(s.burnoutIndex());
        ws.setSleepScore(s.sleepScore());
        ws.setWellbeingScore(s.wellbeingScore());
        ws.setSocialBalanceScore(s.socialBalanceScore());
        ws.setProductivityScore(s.productivityScore());
        ws.setOverallWellnessScore(s.overallWellnessScore());
        ws.setRiskLevel(s.riskLevel());
        return wellnessScoreRepository.save(ws);
    }

    private AvatarState saveAvatar(Long userId, LocalDate date, AiAvatarResponse av) {
        AvatarState a = avatarStateRepository
                .findFirstByUserIdAndLogDateOrderByCreatedAtDesc(userId, date)
                .orElseGet(AvatarState::new);
        a.setUserId(userId);
        a.setLogDate(date);
        a.setState(av.state());
        a.setAnimation(av.animation());
        a.setCaptionEn(av.captionEn());
        a.setCaptionMk(av.captionMk());
        try {
            a.setAttributesJson(objectMapper.writeValueAsString(av.attributes()));
        } catch (Exception e) {
            a.setAttributesJson(null);
        }
        return avatarStateRepository.save(a);
    }

    private List<Recommendation> saveRecommendations(DailyLog dailyLog, Long userId, LocalDate date,
                                                     AiRecommendResponse rec) {
        recommendationRepository.deleteByDailyLogId(dailyLog.getId());
        List<Recommendation> saved = rec.recommendations().stream().map(r -> {
            Recommendation entity = new Recommendation();
            entity.setUserId(userId);
            entity.setDailyLogId(dailyLog.getId());
            entity.setLogDate(date);
            entity.setCategory(r.category());
            entity.setSeverity(r.severity());
            entity.setTextEn(r.textEn());
            entity.setTextMk(r.textMk());
            return entity;
        }).toList();
        return recommendationRepository.saveAll(saved);
    }

    private PredictionResponse savePrediction(Long userId, LocalDate date, AiPredictResponse pred) {
        AiPredictions p = pred.predictions();
        PredictionHistory ph = new PredictionHistory();
        ph.setUserId(userId);
        ph.setPredictionDate(date);
        ph.setForDate(date.plusDays(1));
        ph.setMoodTomorrow(p.moodTomorrow());
        ph.setBurnoutTomorrow(p.burnoutTomorrow());
        ph.setStressTomorrow(p.stressTomorrow());
        ph.setSleepQualityTomorrow(p.sleepQualityTomorrow());
        ph.setRecommendedActivity(p.recommendedActivity());
        ph.setModelVersion(pred.modelVersion());
        ph = predictionHistoryRepository.save(ph);

        String en = p.recommendedActivityText() == null ? null : p.recommendedActivityText().textEn();
        String mk = p.recommendedActivityText() == null ? null : p.recommendedActivityText().textMk();
        return PredictionResponse.from(ph, en, mk, pred.featureImportance());
    }
}
