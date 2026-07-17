package com.coderberojgar.LinkedInClone.dto;

import java.time.Instant;

public final class SocialDtos {

    private SocialDtos() {
    }

    public record FollowResponse(Long followId, Long followerUserId, String followerName, Long followingUserId, String followingName, Instant followedAt) {
    }

    public record NotificationResponse(Long notificationId, Long userId, Long actorUserId, String actorName, String type, String message, boolean read, Instant notifiedAt) {
    }
}
