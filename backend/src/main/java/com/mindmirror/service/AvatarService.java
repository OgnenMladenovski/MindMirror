package com.mindmirror.service;

import com.mindmirror.dto.response.AvatarResponse;
import com.mindmirror.repository.AvatarStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvatarService {

    private final AvatarStateRepository avatarStateRepository;

    public AvatarService(AvatarStateRepository avatarStateRepository) {
        this.avatarStateRepository = avatarStateRepository;
    }

    @Transactional(readOnly = true)
    public AvatarResponse current(Long userId) {
        return avatarStateRepository.findTopByUserIdOrderByLogDateDesc(userId)
                .map(AvatarResponse::from)
                .orElseGet(() -> new AvatarResponse("NEUTRAL", "breathing", null,
                        "Log a check-in to bring your avatar to life.",
                        "Внеси проверка за да го оживееш твојот аватар.", LocalDate.now()));
    }

    @Transactional(readOnly = true)
    public List<AvatarResponse> history(Long userId) {
        return avatarStateRepository.findByUserIdOrderByLogDateDesc(userId).stream()
                .map(AvatarResponse::from).toList();
    }
}
