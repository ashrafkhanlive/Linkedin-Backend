package com.coderberojgar.LinkedInClone.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserResponse(Long userId, String email, String name, String location, LocalDate joinDate, Set<String> roles) {
    }

    public record UserUpdateRequest(@NotBlank String name, String location) {
    }
}

