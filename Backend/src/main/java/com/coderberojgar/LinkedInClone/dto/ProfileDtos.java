package com.coderberojgar.LinkedInClone.dto;

import jakarta.validation.constraints.Size;

public final class ProfileDtos {

    private ProfileDtos() {
    }

    public record ProfileRequest(
            @Size(max = 180) String headline,
            String summary,
            @Size(max = 120) String industry,
            @Size(max = 300) String website
    ) {
    }

    public record ProfileImageRequest(@Size(max = 500) String imageUrl) {
    }

    public record ProfileResponse(
            Long profileId,
            Long userId,
            String name,
            String headline,
            String summary,
            String industry,
            String website,
            String profileImageUrl,
            String coverImageUrl
    ) {
    }
}
