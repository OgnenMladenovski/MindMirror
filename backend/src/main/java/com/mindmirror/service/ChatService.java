package com.mindmirror.service;

import com.mindmirror.client.AiServiceClient;
import com.mindmirror.client.dto.AiDtos.AiChatRequest;
import com.mindmirror.client.dto.AiDtos.AiChatResponse;
import com.mindmirror.client.dto.AiDtos.AiDailyEntry;
import com.mindmirror.client.dto.AiDtos.AiScores;
import com.mindmirror.dto.response.ChatMessageResponse;
import com.mindmirror.entity.User;
import com.mindmirror.entity.WellnessScore;
import com.mindmirror.repository.DailyLogRepository;
import com.mindmirror.repository.WellnessScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ChatService {

    private final DailyLogRepository dailyLogRepository;
    private final WellnessScoreRepository wellnessScoreRepository;
    private final AiServiceClient aiClient;
    private final UserService userService;

    public ChatService(DailyLogRepository dailyLogRepository,
                       WellnessScoreRepository wellnessScoreRepository,
                       AiServiceClient aiClient, UserService userService) {
        this.dailyLogRepository = dailyLogRepository;
        this.wellnessScoreRepository = wellnessScoreRepository;
        this.aiClient = aiClient;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public ChatMessageResponse chat(Long userId, String message, String lang) {
        User user = userService.require(userId);
        String locale = lang != null ? ("mk".equalsIgnoreCase(lang) ? "mk" : "en") : user.getLocale();

        LocalDate today = LocalDate.now();
        List<AiDailyEntry> history = AiServiceClient.toEntries(
                dailyLogRepository.findByUserIdAndLogDateBetweenOrderByLogDate(userId, today.minusDays(30), today));

        AiScores scores = wellnessScoreRepository.findTopByUserIdOrderByLogDateDesc(userId)
                .map(this::toAiScores).orElse(null);

        AiChatResponse resp = aiClient.chat(new AiChatRequest(message, history, scores, locale));
        return new ChatMessageResponse(resp.reply(), resp.replyEn(), resp.replyMk(),
                resp.intent(), resp.backend());
    }

    private AiScores toAiScores(WellnessScore w) {
        return new AiScores(w.getBurnoutIndex(), w.getSleepScore(), w.getWellbeingScore(),
                w.getSocialBalanceScore(), w.getProductivityScore(), w.getOverallWellnessScore(),
                w.getRiskLevel());
    }
}
