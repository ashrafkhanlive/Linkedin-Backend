package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    Page<UserResponse> users(String query, Pageable pageable);

    Page<PostResponse> posts(String query, Pageable pageable);
}
