package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.constant.RoleName;
import com.coderberojgar.LinkedInClone.constant.TokenType;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.AuthResponse;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.ForgotPasswordRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.LoginRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.RegisterRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.ResetPasswordRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.TokenRefreshRequest;
import com.coderberojgar.LinkedInClone.dto.AuthDtos.VerifyEmailRequest;
import com.coderberojgar.LinkedInClone.entity.Profile;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.entity.VerificationToken;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.repository.UserRepository;
import com.coderberojgar.LinkedInClone.repository.VerificationTokenRepository;
import com.coderberojgar.LinkedInClone.security.jwt.JwtProperties;
import com.coderberojgar.LinkedInClone.security.jwt.JwtService;
import com.coderberojgar.LinkedInClone.service.AuthService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(UserRepository userRepository,
                           VerificationTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .location(request.location())
                .joinDate(LocalDate.now())
                .enabled(false)
                .roles(Set.of(RoleName.USER))
                .build();
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setHeadline("");
        user.setProfile(profile);
        User saved = userRepository.save(user);
        VerificationToken verification = createToken(saved, TokenType.EMAIL_VERIFICATION, 24 * 60 * 60 * 1000L);
        log.info("Email verification token generated for {}: {}", saved.getEmail(), verification.getToken());
        return toAuthResponse(saved, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(TokenRefreshRequest request) {
        VerificationToken stored = tokenRepository.findByTokenAndType(request.refreshToken(), TokenType.REFRESH_TOKEN)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        if (stored.getUsedAt() != null || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token expired or revoked");
        }
        String email = jwtService.extractUsername(request.refreshToken());
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toAuthResponse(user, jwtService.generateAccessToken(user), request.refreshToken());
    }

    @Override
    public AuthResponse loginWithGoogle(String email, String name) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Google account email is required");
        }
        User user = userRepository.findByEmailAndDeletedFalse(email.toLowerCase())
                .orElseGet(() -> {
                    User created = User.builder()
                            .email(email.toLowerCase())
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .name(name == null || name.isBlank() ? email : name)
                            .joinDate(LocalDate.now())
                            .enabled(true)
                            .roles(Set.of(RoleName.USER))
                            .build();
                    Profile profile = new Profile();
                    profile.setUser(created);
                    created.setProfile(profile);
                    return userRepository.save(created);
                });
        if (!user.isEnabled()) {
            user.setEnabled(true);
        }
        return issueTokens(user);
    }

    @Override
    public void logout(String refreshToken) {
        tokenRepository.findByTokenAndType(refreshToken, TokenType.REFRESH_TOKEN).ifPresent(token -> {
            token.setUsedAt(Instant.now());
            tokenRepository.save(token);
        });
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailAndDeletedFalse(request.email().toLowerCase()).ifPresent(user -> {
            VerificationToken token = createToken(user, TokenType.PASSWORD_RESET, 30 * 60 * 1000L);
            log.info("Password reset token generated for {}: {}", user.getEmail(), token.getToken());
        });
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        VerificationToken token = getUsableToken(request.token(), TokenType.PASSWORD_RESET);
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        token.setUsedAt(Instant.now());
    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {
        VerificationToken token = getUsableToken(request.token(), TokenType.EMAIL_VERIFICATION);
        token.getUser().setEnabled(true);
        token.setUsedAt(Instant.now());
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(refreshToken);
        token.setType(TokenType.REFRESH_TOKEN);
        token.setExpiresAt(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()));
        tokenRepository.save(token);
        return toAuthResponse(user, accessToken, refreshToken);
    }

    private VerificationToken createToken(User user, TokenType type, long expirationMs) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setType(type);
        token.setExpiresAt(Instant.now().plusMillis(expirationMs));
        return tokenRepository.save(token);
    }

    private VerificationToken getUsableToken(String tokenValue, TokenType type) {
        VerificationToken token = tokenRepository.findByTokenAndType(tokenValue, type)
                .orElseThrow(() -> new BadRequestException("Invalid token"));
        if (token.getUsedAt() != null || token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Token expired or already used");
        }
        return token;
    }

    private AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getName(), roles, accessToken, refreshToken);
    }
}
