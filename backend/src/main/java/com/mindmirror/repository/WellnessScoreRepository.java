package com.mindmirror.repository;

import com.mindmirror.entity.WellnessScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WellnessScoreRepository extends JpaRepository<WellnessScore, Long> {
    Optional<WellnessScore> findByDailyLogId(Long dailyLogId);
    Optional<WellnessScore> findTopByUserIdOrderByLogDateDesc(Long userId);
    List<WellnessScore> findByUserIdAndLogDateBetweenOrderByLogDate(Long userId, LocalDate from, LocalDate to);
    List<WellnessScore> findByUserIdOrderByLogDateDesc(Long userId);

    @Query("select avg(w.overallWellnessScore) from WellnessScore w")
    Double averageOverallWellness();

    @Query("select avg(w.overallWellnessScore) from WellnessScore w where w.userId = :userId")
    Double averageOverallWellnessForUser(Long userId);

    @Query("select w.riskLevel, count(w) from WellnessScore w group by w.riskLevel")
    List<Object[]> countByRiskLevel();
}
