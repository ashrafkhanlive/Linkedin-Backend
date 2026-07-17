package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostPostIdAndDeletedFalseOrderByCommentDateAsc(Long postId);
}
