package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Long> {

    long countByPostPostId(Long postId);
}
