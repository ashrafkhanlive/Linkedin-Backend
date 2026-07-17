package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserUserIdAndDeletedFalse(Long userId);
}
