package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.constant.TokenType;
import com.coderberojgar.LinkedInClone.entity.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByTokenAndType(String token, TokenType type);
}
