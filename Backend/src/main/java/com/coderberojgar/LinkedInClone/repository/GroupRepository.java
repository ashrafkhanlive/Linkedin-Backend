package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.LinkedInGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<LinkedInGroup, Long> {

    Page<LinkedInGroup> findByGroupNameContainingIgnoreCaseAndDeletedFalse(String groupName, Pageable pageable);
}
