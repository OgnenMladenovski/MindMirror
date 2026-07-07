package com.mindmirror.client;

import com.mindmirror.client.dto.AiDtos.*;
import com.mindmirror.entity.DailyLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/** Thin, resilient wrapper around the FastAPI AI microservice. */
@Component
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);
    private final RestClient client;

    public AiServiceClient(RestClient aiRestClient) {
        this.client = aiRestClient;
    }

    public AiScores analyze(AiAnalyzeRequest request) {
        return post("/analyze", request, AiScores.class);
    }

    public AiRecommendResponse recommend(List<AiDailyEntry> history, String lang) {
        return post("/recommend", new AiHistoryRequest(history, lang), AiRecommendResponse.class);
    }

    public AiTrendsResponse trends(List<AiDailyEntry> history, String lang) {
        return post("/trends", new AiHistoryRequest(history, lang), AiTrendsResponse.class);
    }

    public AiAvatarResponse avatarState(AiAvatarRequest request) {
        return post("/avatar-state", request, AiAvatarResponse.class);
    }

    public AiPredictResponse predict(List<AiDailyEntry> history, String lang) {
        return post("/predict", new AiHistoryRequest(history, lang), AiPredictResponse.class);
    }

    public AiChatResponse chat(AiChatRequest request) {
        return post("/chat", request, AiChatResponse.class);
    }

    public boolean isHealthy() {
        try {
            client.get().uri("/health").retrieve().toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private <T> T post(String path, Object body, Class<T> type) {
        try {
            return client.post().uri(path).body(body).retrieve().body(type);
        } catch (Exception e) {
            log.warn("AI service call to {} failed: {}", path, e.getMessage());
            throw new AiServiceException("AI service unavailable for " + path, e);
        }
    }

    // --- entity -> AI DTO mapping -------------------------------------------

    public static AiDailyEntry toEntry(DailyLog l) {
        return new AiDailyEntry(
                l.getLogDate() == null ? null : l.getLogDate().toString(),
                l.getSleepHours(), l.getStressLevel(), l.getMoodScore(),
                l.getPhysicalActivityMin(), l.getWaterIntake(), l.getScreenTimeHours(),
                l.getStudyHours(), l.getSocialTimeMin(), l.getEnergyLevel(),
                l.getNutritionQuality(), l.getNotes());
    }

    public static List<AiDailyEntry> toEntries(List<DailyLog> logs) {
        return logs.stream().map(AiServiceClient::toEntry).toList();
    }
}
