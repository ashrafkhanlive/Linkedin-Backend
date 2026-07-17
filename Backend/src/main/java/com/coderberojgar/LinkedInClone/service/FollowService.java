package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.SocialDtos.FollowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    FollowResponse follow(Long userId);

    void unfollow(Long userId);

    Page<FollowResponse> followers(Long userId, Pageable pageable);

    Page<FollowResponse> following(Long userId, Pageable pageable);
}
