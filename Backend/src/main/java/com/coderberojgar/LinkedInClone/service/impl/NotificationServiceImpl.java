package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.SocialDtos.NotificationResponse;
import com.coderberojgar.LinkedInClone.entity.Notification;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.SocialMapper;
import com.coderberojgar.LinkedInClone.repository.NotificationRepository;
import com.coderberojgar.LinkedInClone.service.NotificationService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final SocialMapper mapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserService userService, SocialMapper mapper) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public NotificationResponse create(Long userId, Long actorId, String type, String message) {
        User user = userService.getUser(userId);
        User actor = actorId == null ? null : userService.getUser(actorId);
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setActor(actor);
        notification.setType(type);
        notification.setMessage(message);
        notification.setNotifiedAt(Instant.now());
        return mapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> currentUserNotifications(Pageable pageable) {
        return notificationRepository.findByUserUserIdAndDeletedFalseOrderByNotifiedAtDesc(userService.currentUser().getUserId(), pageable)
                .map(mapper::toNotificationResponse);
    }

    @Override
    public NotificationResponse markRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getUserId().equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("You can only read your own notifications");
        }
        notification.setRead(true);
        return mapper.toNotificationResponse(notification);
    }
}
