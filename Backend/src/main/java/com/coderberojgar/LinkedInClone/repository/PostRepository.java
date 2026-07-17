package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByDeletedFalseOrderByPostDateDesc(Pageable pageable);

    Page<Post> findByUserUserIdAndDeletedFalseOrderByPostDateDesc(Long userId, Pageable pageable);

    Page<Post> findByContentContainingIgnoreCaseAndDeletedFalse(String content, Pageable pageable);
}
