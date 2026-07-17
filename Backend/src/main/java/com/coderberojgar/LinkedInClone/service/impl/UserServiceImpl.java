package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserUpdateRequest;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.UserMapper;
import com.coderberojgar.LinkedInClone.repository.UserRepository;
import com.coderberojgar.LinkedInClone.security.UserPrincipal;
import com.coderberojgar.LinkedInClone.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return getUser(userPrincipal.getId());
        }
        throw new ResourceNotFoundException("Current user not found");
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse currentUserResponse() {
        return userMapper.toResponse(currentUser());
    }

    @Override
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        User user = currentUser();
        user.setName(request.name());
        user.setLocation(request.location());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String query, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCaseAndDeletedFalse(query == null ? "" : query, pageable)
                .map(userMapper::toResponse);
    }
}
