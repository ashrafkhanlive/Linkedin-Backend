package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentResponse;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostRequest;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.entity.Comment;
import com.coderberojgar.LinkedInClone.entity.Post;
import com.coderberojgar.LinkedInClone.entity.PostLike;
import com.coderberojgar.LinkedInClone.entity.Share;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.PostMapper;
import com.coderberojgar.LinkedInClone.repository.CommentRepository;
import com.coderberojgar.LinkedInClone.repository.PostLikeRepository;
import com.coderberojgar.LinkedInClone.repository.PostRepository;
import com.coderberojgar.LinkedInClone.repository.ShareRepository;
import com.coderberojgar.LinkedInClone.service.PostService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final ShareRepository shareRepository;
    private final UserService userService;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository,
                           CommentRepository commentRepository,
                           PostLikeRepository likeRepository,
                           ShareRepository shareRepository,
                           UserService userService,
                           PostMapper postMapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.shareRepository = shareRepository;
        this.userService = userService;
        this.postMapper = postMapper;
    }

    @Override
    public PostResponse create(PostRequest request) {
        Post post = new Post();
        post.setUser(userService.currentUser());
        post.setContent(request.content());
        post.setImageUrl(request.imageUrl());
        post.setVideoUrl(request.videoUrl());
        post.setPostDate(Instant.now());
        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    public PostResponse update(Long postId, PostRequest request) {
        Post post = ownPost(postId);
        post.setContent(request.content());
        post.setImageUrl(request.imageUrl());
        post.setVideoUrl(request.videoUrl());
        return postMapper.toResponse(post);
    }

    @Override
    public void delete(Long postId) {
        ownPost(postId).setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> feed(Pageable pageable) {
        return postRepository.findByDeletedFalseOrderByPostDateDesc(pageable).map(postMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> byUser(Long userId, Pageable pageable) {
        return postRepository.findByUserUserIdAndDeletedFalseOrderByPostDateDesc(userId, pageable).map(postMapper::toResponse);
    }

    @Override
    public void like(Long postId) {
        User user = userService.currentUser();
        if (likeRepository.findByPostPostIdAndUserUserId(postId, user.getUserId()).isPresent()) {
            return;
        }
        PostLike like = new PostLike();
        like.setPost(getPost(postId));
        like.setUser(user);
        likeRepository.save(like);
    }

    @Override
    public void unlike(Long postId) {
        User user = userService.currentUser();
        likeRepository.findByPostPostIdAndUserUserId(postId, user.getUserId()).ifPresent(likeRepository::delete);
    }

    @Override
    public CommentResponse addComment(Long postId, CommentRequest request) {
        Comment comment = new Comment();
        comment.setPost(getPost(postId));
        comment.setUser(userService.currentUser());
        comment.setContent(request.content());
        comment.setCommentDate(Instant.now());
        if (request.parentCommentId() != null) {
            comment.setParentComment(getComment(request.parentCommentId()));
        }
        return postMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = ownComment(commentId);
        comment.setContent(request.content());
        return postMapper.toCommentResponse(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        ownComment(commentId).setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> comments(Long postId) {
        return commentRepository.findByPostPostIdAndDeletedFalseOrderByCommentDateAsc(postId).stream()
                .map(postMapper::toCommentResponse)
                .toList();
    }

    @Override
    public PostResponse share(Long postId) {
        Share share = new Share();
        Post post = getPost(postId);
        share.setPost(post);
        share.setUser(userService.currentUser());
        share.setSharedAt(Instant.now());
        shareRepository.save(share);
        return postMapper.toResponse(post);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .filter(post -> !post.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    private Post ownPost(Long postId) {
        Post post = getPost(postId);
        if (!post.getUser().getUserId().equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("You can only modify your own posts");
        }
        return post;
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .filter(comment -> !comment.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    private Comment ownComment(Long commentId) {
        Comment comment = getComment(commentId);
        if (!comment.getUser().getUserId().equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("You can only modify your own comments");
        }
        return comment;
    }
}
