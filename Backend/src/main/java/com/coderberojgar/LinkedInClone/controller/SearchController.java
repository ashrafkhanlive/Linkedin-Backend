package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.SEARCH)
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> users(@RequestParam(defaultValue = "") String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Users found", searchService.users(q, pageable)));
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> posts(@RequestParam(defaultValue = "") String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Posts found", searchService.posts(q, pageable)));
    }
}

