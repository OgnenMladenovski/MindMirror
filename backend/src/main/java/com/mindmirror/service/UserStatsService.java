package com.mindmirror.service;

import com.mindmirror.entity.UserStats;
import com.mindmirror.repository.UserStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/** Central gamification bookkeeping: XP, level and daily streaks. */
@Service
public class UserStatsService {

    private static final int XP_PER_CHECKIN = 10;

    private final UserStatsRepository statsRepository;

    public UserStatsService(UserStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    public UserStats getOrCreate(Long userId) {
        return statsRepository.findByUserId(userId)
                .orElseGet(() -> statsRepository.save(new UserStats(userId)));
    }

    /** Update streak + award check-in XP the first time a given date is logged. */
    @Transactional
    public void recordLog(Long userId, LocalDate date) {
        UserStats stats = getOrCreate(userId);
        LocalDate last = stats.getLastLogDate();
        if (last != null && last.isEqual(date)) {
            return; // already counted this day
        }
        if (last != null && last.plusDays(1).isEqual(date)) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else {
            stats.setCurrentStreak(1);
        }
        stats.setLongestStreak(Math.max(stats.getLongestStreak(), stats.getCurrentStreak()));
        stats.setLastLogDate(date);
        stats.setTotalXp(stats.getTotalXp() + XP_PER_CHECKIN);
        stats.setLevel(1 + stats.getTotalXp() / 100);
        statsRepository.save(stats);
    }

    @Transactional
    public void addXp(Long userId, int amount) {
        UserStats stats = getOrCreate(userId);
        stats.setTotalXp(stats.getTotalXp() + amount);
        stats.setLevel(1 + stats.getTotalXp() / 100);
        statsRepository.save(stats);
    }
}
