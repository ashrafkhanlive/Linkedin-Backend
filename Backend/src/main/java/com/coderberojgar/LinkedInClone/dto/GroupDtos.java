package com.coderberojgar.LinkedInClone.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public final class GroupDtos {

    private GroupDtos() {
    }

    public record GroupRequest(@NotBlank String groupName, String description) {
    }

    public record GroupResponse(Long groupId, Long ownerId, String groupName, String description, Instant createdDate, long memberCount) {
    }
}
