package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentResponse;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponse create(PostRequest request);

    PostResponse update(Long postId, PostRequest request);

    void delete(Long postId);

    Page<PostResponse> feed(Pageable pageable);

    Page<PostResponse> byUser(Long userId, Pageable pageable);

    void like(Long postId);

    void unlike(Long postId);

    CommentResponse addComment(Long postId, CommentRequest request);

    CommentResponse updateComment(Long commentId, CommentRequest request);

    void deleteComment(Long commentId);

    List<CommentResponse> comments(Long postId);

    PostResponse share(Long postId);
}
