package com.coderberojgar.LinkedInClone.entity;

import com.coderberojgar.LinkedInClone.constant.ConnectionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "connections", indexes = @Index(name = "idx_connections_users", columnList = "user_id,connection_user_id", unique = true))
public class Connection extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Long connectionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connection_user_id", nullable = false)
    private User connectionUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_status", nullable = false, length = 30)
    private ConnectionStatus connectionStatus;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;
}
