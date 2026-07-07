package com.mindmirror.repository;

import com.mindmirror.entity.AvatarState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvatarStateRepository extends JpaRepository<AvatarState, Long> {
    Optional<AvatarState> findTopByUserIdOrderByLogDateDesc(Long userId);
    Optional<AvatarState> findFirstByUserIdAndLogDateOrderByCreatedAtDesc(Long userId, java.time.LocalDate logDate);
    List<AvatarState> findByUserIdOrderByLogDateDesc(Long userId);
}
