package com.coderberojgar.LinkedInClone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class PostDtos {

    private PostDtos() {
    }

    public record PostRequest(@NotBlank String content, @Size(max = 500) String imageUrl, @Size(max = 500) String videoUrl) {
    }

    public record PostResponse(Long postId, Long userId, String authorName, String content, Instant postDate, String imageUrl, String videoUrl, long likes, long shares) {
    }

    public record CommentRequest(@NotBlank String content, Long parentCommentId) {
    }

    public record CommentResponse(Long commentId, Long postId, Long userId, String authorName, Long parentCommentId, String content, Instant commentDate) {
    }
}

