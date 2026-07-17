package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.PostDtos.CommentResponse;
import com.coderberojgar.LinkedInClone.dto.PostDtos.PostResponse;
import com.coderberojgar.LinkedInClone.entity.Comment;
import com.coderberojgar.LinkedInClone.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "authorName", source = "user.name")
    @Mapping(target = "likes", expression = "java(post.getLikes() == null ? 0L : post.getLikes().size())")
    @Mapping(target = "shares", expression = "java(post.getShares() == null ? 0L : post.getShares().size())")
    PostResponse toResponse(Post post);

    @Mapping(target = "postId", source = "post.postId")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "authorName", source = "user.name")
    @Mapping(target = "parentCommentId", source = "parentComment.commentId")
    CommentResponse toCommentResponse(Comment comment);
}
