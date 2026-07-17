package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserUpdateRequest;
import com.coderberojgar.LinkedInClone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User currentUser();

    User getUser(Long userId);

    UserResponse currentUserResponse();

    UserResponse updateCurrentUser(UserUpdateRequest request);

    Page<UserResponse> search(String query, Pageable pageable);
}
