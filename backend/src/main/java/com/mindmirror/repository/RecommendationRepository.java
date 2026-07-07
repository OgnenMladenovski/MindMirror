package com.mindmirror.repository;

import com.mindmirror.entity.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Recommendation> findByDailyLogId(Long dailyLogId);
    void deleteByDailyLogId(Long dailyLogId);
}
