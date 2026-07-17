package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Skill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByUserUserIdAndDeletedFalseOrderBySkillNameAsc(Long userId);

    boolean existsByUserUserIdAndSkillNameIgnoreCase(Long userId, String skillName);
}
