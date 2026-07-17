package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileImageRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PROFILES)
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> get(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Profile found", profileService.getByUserId(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> update(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profileService.updateCurrent(request)));
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<ProfileResponse>> profileImage(@Valid @RequestBody ProfileImageRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile image updated", profileService.updateProfileImage(request)));
    }

    @PutMapping("/me/cover-image")
    public ResponseEntity<ApiResponse<ProfileResponse>> coverImage(@Valid @RequestBody ProfileImageRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cover image updated", profileService.updateCoverImage(request)));
    }
}
