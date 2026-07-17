package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.SocialDtos.NotificationResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Notifications loaded", notificationService.currentUserNotifications(pageable)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(ApiResponse.success("Notification marked read", notificationService.markRead(notificationId)));
    }
}

