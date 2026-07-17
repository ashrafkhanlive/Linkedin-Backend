package com.coderberojgar.LinkedInClone.dto;

import com.coderberojgar.LinkedInClone.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @ValidPassword String password,
            @NotBlank String name,
            String location
    ) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record TokenRefreshRequest(@NotBlank String refreshToken) {
    }

    public record ForgotPasswordRequest(@Email @NotBlank String email) {
    }

    public record ResetPasswordRequest(@NotBlank String token, @ValidPassword String newPassword) {
    }

    public record VerifyEmailRequest(@NotBlank String token) {
    }

    public record AuthResponse(
            Long userId,
            String email,
            String name,
            Set<String> roles,
            String accessToken,
            String refreshToken
    ) {
    }

    public record ChangeRoleRequest(@NotNull Long userId, @NotBlank String role) {
    }
}

