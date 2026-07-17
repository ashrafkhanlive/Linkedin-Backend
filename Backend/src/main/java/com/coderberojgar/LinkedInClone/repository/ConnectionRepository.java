package com.coderberojgar.LinkedInClone.repository;

import com.coderberojgar.LinkedInClone.constant.ConnectionStatus;
import com.coderberojgar.LinkedInClone.entity.Connection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    Optional<Connection> findByUserUserIdAndConnectionUserUserId(Long userId, Long connectionUserId);

    Page<Connection> findByConnectionUserUserIdAndConnectionStatusAndDeletedFalse(Long userId, ConnectionStatus status, Pageable pageable);

    Page<Connection> findByUserUserIdAndConnectionStatusAndDeletedFalse(Long userId, ConnectionStatus status, Pageable pageable);
}
