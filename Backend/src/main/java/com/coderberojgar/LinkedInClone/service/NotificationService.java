package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.SocialDtos.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse create(Long userId, Long actorId, String type, String message);

    Page<NotificationResponse> currentUserNotifications(Pageable pageable);

    NotificationResponse markRead(Long notificationId);
}
