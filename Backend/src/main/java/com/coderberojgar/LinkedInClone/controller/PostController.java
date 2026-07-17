package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentResponse;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.POSTS)
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Post created", postService.create(request)));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> update(@PathVariable Long postId, @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Post updated", postService.update(postId, request)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.ok(ApiResponse.success("Post deleted"));
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> feed(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Feed loaded", postService.feed(pageable)));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> byUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("User posts loaded", postService.byUser(userId, pageable)));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> like(@PathVariable Long postId) {
        postService.like(postId);
        return ResponseEntity.ok(ApiResponse.success("Post liked"));
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<Void>> unlike(@PathVariable Long postId) {
        postService.unlike(postId);
        return ResponseEntity.ok(ApiResponse.success("Post unliked"));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(@PathVariable Long postId, @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Comment added", postService.addComment(postId, request)));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> comments(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success("Comments loaded", postService.comments(postId)));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Comment updated", postService.updateComment(commentId, request)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted"));
    }

    @PostMapping("/{postId}/shares")
    public ResponseEntity<ApiResponse<PostResponse>> share(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success("Post shared", postService.share(postId)));
    }
}

