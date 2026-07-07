package com.mindmirror.repository;

import com.mindmirror.entity.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {
    Optional<PredictionHistory> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    List<PredictionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
