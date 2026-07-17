package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.SocialDtos.FollowResponse;
import com.coderberojgar.LinkedInClone.entity.Follow;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.mapper.SocialMapper;
import com.coderberojgar.LinkedInClone.repository.FollowRepository;
import com.coderberojgar.LinkedInClone.service.FollowService;
import com.coderberojgar.LinkedInClone.service.NotificationService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final SocialMapper mapper;

    public FollowServiceImpl(FollowRepository followRepository, UserService userService, NotificationService notificationService, SocialMapper mapper) {
        this.followRepository = followRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Override
    public FollowResponse follow(Long userId) {
        User current = userService.currentUser();
        if (current.getUserId().equals(userId)) {
            throw new BadRequestException("Cannot follow yourself");
        }
        User target = userService.getUser(userId);
        Follow follow = followRepository.findByFollowerUserIdAndFollowingUserId(current.getUserId(), userId)
                .orElseGet(() -> {
                    Follow created = new Follow();
                    created.setFollower(current);
                    created.setFollowing(target);
                    created.setFollowedAt(Instant.now());
                    notificationService.create(userId, current.getUserId(), "FOLLOW", current.getName() + " started following you");
                    return followRepository.save(created);
                });
        if (follow.isDeleted()) {
            follow.setDeleted(false);
            follow.setFollowedAt(Instant.now());
        }
        return mapper.toFollowResponse(follow);
    }

    @Override
    public void unfollow(Long userId) {
        User current = userService.currentUser();
        followRepository.findByFollowerUserIdAndFollowingUserId(current.getUserId(), userId)
                .ifPresent(follow -> follow.setDeleted(true));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> followers(Long userId, Pageable pageable) {
        return followRepository.findByFollowingUserIdAndDeletedFalse(userId, pageable).map(mapper::toFollowResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowResponse> following(Long userId, Pageable pageable) {
        return followRepository.findByFollowerUserIdAndDeletedFalse(userId, pageable).map(mapper::toFollowResponse);
    }
}
