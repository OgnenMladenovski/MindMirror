package com.mindmirror.repository;

import com.mindmirror.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);
    List<DailyLog> findByUserIdOrderByLogDateDesc(Long userId);
    List<DailyLog> findByUserIdAndLogDateBetweenOrderByLogDate(Long userId, LocalDate from, LocalDate to);
    Optional<DailyLog> findTopByUserIdOrderByLogDateDesc(Long userId);
    long countByUserId(Long userId);

    /** Global averages: [avgSleepHours, avgScreenTimeHours, avgActivityMin, avgMood, avgStress]. */
    @Query("select avg(l.sleepHours), avg(l.screenTimeHours), avg(l.physicalActivityMin), "
            + "avg(l.moodScore), avg(l.stressLevel) from DailyLog l")
    List<Object[]> globalAverages();
}
