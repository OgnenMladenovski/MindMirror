package com.mindmirror.service;

import com.mindmirror.dto.response.NotificationResponse;
import com.mindmirror.entity.Notification;
import com.mindmirror.entity.enums.NotificationType;
import com.mindmirror.exception.NotFoundException;
import com.mindmirror.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification create(Long userId, NotificationType type,
                               String titleEn, String titleMk, String bodyEn, String bodyMk) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitleEn(titleEn);
        n.setTitleMk(titleMk);
        n.setBodyEn(bodyEn);
        n.setBodyMk(bodyMk);
        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFlagFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(Long userId, Long id) {
        Notification n = notificationRepository.findById(id)
                .filter(x -> x.getUserId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        n.setReadFlag(true);
        return NotificationResponse.from(notificationRepository.save(n));
    }
}
