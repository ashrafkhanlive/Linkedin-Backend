package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Experience;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUserUserIdAndDeletedFalseOrderByStartDateDesc(Long userId);
}
