package com.smartplacementai.service;

import com.smartplacementai.dto.*;
import com.smartplacementai.exception.EmailAlreadyExistsException;
import com.smartplacementai.exception.InvalidCredentialsException;
import com.smartplacementai.exception.InvalidTokenException;
import com.smartplacementai.model.mongo.LoginAuditDocument;
import com.smartplacementai.model.sql.EmailToken;
import com.smartplacementai.model.sql.RefreshToken;
import com.smartplacementai.model.sql.Role;
import com.smartplacementai.model.sql.User;
import com.smartplacementai.repository.mongo.LoginAuditRepository;
import com.smartplacementai.repository.sql.EmailTokenRepository;
import com.smartplacementai.repository.sql.RefreshTokenRepository;
import com.smartplacementai.repository.sql.UserRepository;
import com.smartplacementai.security.JwtUtil;
import com.smartplacementai.security.TokenHashUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenHashUtil tokenHashUtil;
    private final EmailService emailService;

    private static final long REFRESH_TOKEN_DAYS = 30;
    private static final long VERIFY_TOKEN_HOURS = 24;
    private static final long RESET_TOKEN_HOURS = 1;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                        EmailTokenRepository emailTokenRepository, LoginAuditRepository loginAuditRepository,
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenHashUtil tokenHashUtil,
                        EmailService emailService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.loginAuditRepository = loginAuditRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenHashUtil = tokenHashUtil;
        this.emailService = emailService;
    }

    public static class AuthResult {
        public final AuthResponse response;
        public final String rawRefreshToken;
        public AuthResult(AuthResponse response, String rawRefreshToken) {
            this.response = response; this.rawRefreshToken = rawRefreshToken;
        }
    }

    public AuthResult register(RegisterRequest request, String deviceInfo, String ip) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        User saved = userRepository.save(user);

        issueVerificationEmail(saved);

        String rawRefresh = createRefreshToken(saved.getId(), deviceInfo, ip);
        AuthResponse response = buildAuthResponse(saved);
        return new AuthResult(response, rawRefresh);
    }

    public AuthResult login(LoginRequest request, String deviceInfo, String ip) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    loginAuditRepository.save(new LoginAuditDocument(request.getEmail(), "LOGIN_FAILED", ip));
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginAuditRepository.save(new LoginAuditDocument(request.getEmail(), "LOGIN_FAILED", ip));
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String rawRefresh = createRefreshToken(user.getId(), deviceInfo, ip);
        AuthResponse response = buildAuthResponse(user);
        return new AuthResult(response, rawRefresh);
    }

    /** Rotation: the presented refresh token is revoked and a brand new one issued, every time. */
    public AuthResult refresh(String rawRefreshToken, String deviceInfo, String ip) {
        String hash = tokenHashUtil.hash(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (existing.isRevoked() || existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expired or revoked — please log in again");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        User user = userRepository.findById(existing.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User no longer exists"));

        String newRawRefresh = createRefreshToken(user.getId(), deviceInfo, ip);
        AuthResponse response = buildAuthResponse(user);
        return new AuthResult(response, newRawRefresh);
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null) return;
        String hash = tokenHashUtil.hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public void logoutAll(Long userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    public List<SessionDto> listSessions(Long userId) {
        return refreshTokenRepository.findByUserIdAndRevokedFalse(userId).stream()
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(t -> new SessionDto(t.getId(), t.getDeviceInfo(), t.getIpAddress(), t.getCreatedAt(), t.getExpiresAt()))
                .toList();
    }

    public void revokeSession(Long userId, Long sessionId) {
        RefreshToken token = refreshTokenRepository.findById(sessionId)
                .orElseThrow(() -> new InvalidTokenException("Session not found"));
        if (!token.getUserId().equals(userId)) {
            throw new InvalidTokenException("Session not found"); // don't reveal it belongs to someone else
        }
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void verifyEmail(String rawToken) {
        EmailToken token = consumeEmailToken(rawToken, "VERIFY");
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User no longer exists"));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public void resendVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("User no longer exists"));
        if (!user.isEmailVerified()) {
            issueVerificationEmail(user);
        }
    }

    /** Deliberately identical response whether the email exists or not — no account enumeration. */
    public void forgotPassword(String email, String ip) {
        loginAuditRepository.save(new LoginAuditDocument(email, "PASSWORD_RESET_REQUESTED", ip));
        userRepository.findByEmail(email).ifPresent(user -> {
            String raw = tokenHashUtil.generateRawToken();
            EmailToken token = new EmailToken();
            token.setUserId(user.getId());
            token.setTokenHash(tokenHashUtil.hash(raw));
            token.setTokenType("RESET");
            token.setExpiresAt(LocalDateTime.now().plusHours(RESET_TOKEN_HOURS));
            emailTokenRepository.save(token);
            emailService.sendPasswordResetEmail(user.getEmail(), raw);
        });
    }

    public void resetPassword(String rawToken, String newPassword, String ip) {
        EmailToken token = consumeEmailToken(rawToken, "RESET");
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User no longer exists"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        loginAuditRepository.save(new LoginAuditDocument(user.getEmail(), "PASSWORD_RESET_COMPLETED", ip));

        logoutAll(user.getId()); // force re-login on every device after a password reset
    }

    // ---- internal helpers ----

    private void issueVerificationEmail(User user) {
        String raw = tokenHashUtil.generateRawToken();
        EmailToken token = new EmailToken();
        token.setUserId(user.getId());
        token.setTokenHash(tokenHashUtil.hash(raw));
        token.setTokenType("VERIFY");
        token.setExpiresAt(LocalDateTime.now().plusHours(VERIFY_TOKEN_HOURS));
        emailTokenRepository.save(token);
        emailService.sendVerificationEmail(user.getEmail(), raw);
    }

    private EmailToken consumeEmailToken(String rawToken, String expectedType) {
        String hash = tokenHashUtil.hash(rawToken);
        EmailToken token = emailTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())
                || !token.getTokenType().equals(expectedType)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
        token.setUsed(true);
        emailTokenRepository.save(token);
        return token;
    }

    private String createRefreshToken(Long userId, String deviceInfo, String ip) {
        String raw = tokenHashUtil.generateRawToken();
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setTokenHash(tokenHashUtil.hash(raw));
        token.setDeviceInfo(deviceInfo);
        token.setIpAddress(ip);
        token.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS));
        refreshTokenRepository.save(token);
        return raw;
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return new AuthResponse(accessToken, user.getId(), user.getName(), user.getEmail(),
                user.getRole().name());
    }
}