package com.mindmirror.repository;

import com.mindmirror.entity.Challenge;
import com.mindmirror.entity.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByUserIdAndChallengeDate(Long userId, LocalDate challengeDate);
    List<Challenge> findByUserIdOrderByChallengeDateDesc(Long userId);
    long countByUserIdAndStatus(Long userId, ChallengeStatus status);

    @Query("select c.type, count(c) from Challenge c where c.status = 'COMPLETED' group by c.type order by count(c) desc")
    List<Object[]> countCompletedByType();
}
