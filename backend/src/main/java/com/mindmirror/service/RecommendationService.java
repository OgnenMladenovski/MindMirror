package com.mindmirror.service;

import com.mindmirror.dto.response.RecommendationResponse;
import com.mindmirror.repository.RecommendationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> latest(Long userId, int limit) {
        return recommendationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, Math.max(1, limit)))
                .stream().map(RecommendationResponse::from).toList();
    }
}
