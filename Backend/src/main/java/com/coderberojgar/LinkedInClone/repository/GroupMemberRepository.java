package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.GroupMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupGroupIdAndUserUserId(Long groupId, Long userId);

    long countByGroupGroupId(Long groupId);
}
