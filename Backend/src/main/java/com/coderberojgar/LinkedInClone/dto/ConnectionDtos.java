package com.coderberojgar.LinkedInClone.dto;

import com.coderberojgar.LinkedInClone.constant.ConnectionStatus;
import java.time.Instant;

public final class ConnectionDtos {

    private ConnectionDtos() {
    }

    public record ConnectionResponse(Long connectionId, Long userId, Long connectionUserId, String connectionName, ConnectionStatus status, Instant requestedAt) {
    }
}
