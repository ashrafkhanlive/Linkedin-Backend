package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileImageRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileResponse;
import com.coderberojgar.LinkedInClone.entity.Profile;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.ProfileMapper;
import com.coderberojgar.LinkedInClone.repository.ProfileRepository;
import com.coderberojgar.LinkedInClone.service.ProfileService;
import com.coderberojgar.LinkedInClone.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final ProfileMapper profileMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserService userService, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.userService = userService;
        this.profileMapper = profileMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getByUserId(Long userId) {
        Profile profile = profileRepository.findByUserUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateCurrent(ProfileRequest request) {
        Profile profile = currentProfile();
        profileMapper.update(request, profile);
        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateProfileImage(ProfileImageRequest request) {
        Profile profile = currentProfile();
        profile.setProfileImageUrl(request.imageUrl());
        return profileMapper.toResponse(profile);
    }

    @Override
    public ProfileResponse updateCoverImage(ProfileImageRequest request) {
        Profile profile = currentProfile();
        profile.setCoverImageUrl(request.imageUrl());
        return profileMapper.toResponse(profile);
    }

    private Profile currentProfile() {
        User user = userService.currentUser();
        return profileRepository.findByUserUserIdAndDeletedFalse(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }
}
