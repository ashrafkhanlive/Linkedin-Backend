package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);

    Page<User> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);
}
