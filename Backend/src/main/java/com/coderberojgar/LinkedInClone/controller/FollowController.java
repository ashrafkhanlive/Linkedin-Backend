package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.SocialDtos.FollowResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.FollowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.FOLLOWS)
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FollowResponse>> follow(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User followed", followService.follow(userId)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(@PathVariable Long userId) {
        followService.unfollow(userId);
        return ResponseEntity.ok(ApiResponse.success("User unfollowed"));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<FollowResponse>>> followers(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Followers loaded", followService.followers(userId, pageable)));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Page<FollowResponse>>> following(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Following loaded", followService.following(userId, pageable)));
    }
}

