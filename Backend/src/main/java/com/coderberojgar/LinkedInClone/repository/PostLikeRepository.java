package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostPostIdAndUserUserId(Long postId, Long userId);

    long countByPostPostId(Long postId);
}
