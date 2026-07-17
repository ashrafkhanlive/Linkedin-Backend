package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.AuthDtos.AuthResponse;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.ForgotPasswordRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.LoginRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.RegisterRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.ResetPasswordRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.TokenRefreshRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.VerifyEmailRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(TokenRefreshRequest request);

    AuthResponse loginWithGoogle(String email, String name);

    void logout(String refreshToken);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void verifyEmail(VerifyEmailRequest request);
}
