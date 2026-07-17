package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Follow;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerUserIdAndFollowingUserId(Long followerUserId, Long followingUserId);

    Page<Follow> findByFollowingUserIdAndDeletedFalse(Long userId, Pageable pageable);

    Page<Follow> findByFollowerUserIdAndDeletedFalse(Long userId, Pageable pageable);
}
