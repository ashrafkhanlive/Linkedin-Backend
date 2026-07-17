package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserUpdateRequest;
import com.coderberojgar.LinkedInClone.mapper.UserMapper;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.USERS)
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        return ResponseEntity.ok(ApiResponse.success("Current user", userService.currentUserResponse()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User updated", userService.updateCurrentUser(request)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User profile", userMapper.toResponse(userService.getUser(userId))));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(@RequestParam(defaultValue = "") String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Users found", userService.search(q, pageable)));
    }
}
