package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.mapper.PostMapper;
import com.coderberojgar.LinkedInClone.mapper.UserMapper;
import com.coderberojgar.LinkedInClone.repository.PostRepository;
import com.coderberojgar.LinkedInClone.repository.UserRepository;
import com.coderberojgar.LinkedInClone.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public SearchServiceImpl(UserRepository userRepository, PostRepository postRepository, UserMapper userMapper, PostMapper postMapper) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }

    @Override
    public Page<UserResponse> users(String query, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCaseAndDeletedFalse(query == null ? "" : query, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public Page<PostResponse> posts(String query, Pageable pageable) {
        return postRepository.findByContentContainingIgnoreCaseAndDeletedFalse(query == null ? "" : query, pageable)
                .map(postMapper::toResponse);
    }
}
