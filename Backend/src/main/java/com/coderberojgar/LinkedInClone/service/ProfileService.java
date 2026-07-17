package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileImageRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileResponse;

public interface ProfileService {

    ProfileResponse getByUserId(Long userId);

    ProfileResponse updateCurrent(ProfileRequest request);

    ProfileResponse updateProfileImage(ProfileImageRequest request);

    ProfileResponse updateCoverImage(ProfileImageRequest request);
}
