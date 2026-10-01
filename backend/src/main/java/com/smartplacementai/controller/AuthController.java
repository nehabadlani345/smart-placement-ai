package com.smartplacementai.controller;

import com.smartplacementai.dto.*;
import com.smartplacementai.security.CurrentUser;
import com.smartplacementai.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private static final String REFRESH_COOKIE = "refresh_token";

    public AuthController(AuthService authService, CurrentUser currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletRequest req, HttpServletResponse res) {
        AuthService.AuthResult result = authService.register(request, deviceInfo(req), ip(req));
        setRefreshCookie(res, result.rawRefreshToken);
        return ResponseEntity.ok(result.response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest req, HttpServletResponse res) {
        AuthService.AuthResult result = authService.login(request, deviceInfo(req), ip(req));
        setRefreshCookie(res, result.rawRefreshToken);
        return ResponseEntity.ok(result.response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest req, HttpServletResponse res) {
        String rawRefresh = readRefreshCookie(req);
        AuthService.AuthResult result = authService.refresh(rawRefresh, deviceInfo(req), ip(req));
        setRefreshCookie(res, result.rawRefreshToken);
        return ResponseEntity.ok(result.response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        authService.logout(readRefreshCookie(req));
        clearRefreshCookie(res);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(Authentication authentication, HttpServletResponse res) {
        authService.logoutAll(currentUser.id(authentication));
        clearRefreshCookie(res);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDto>> sessions(Authentication authentication) {
        return ResponseEntity.ok(authService.listSessions(currentUser.id(authentication)));
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> revokeSession(@PathVariable Long id, Authentication authentication) {
        authService.revokeSession(currentUser.id(authentication), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(Authentication authentication) {
        authService.resendVerification(currentUser.id(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest req) {
        authService.forgotPassword(request.getEmail(), ip(req));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest req) {
        authService.resetPassword(request.getToken(), request.getNewPassword(), ip(req));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {
        return ResponseEntity.ok("Authenticated as: " + authentication.getName());
    }

    // ---- cookie + request helpers ----

    private void setRefreshCookie(HttpServletResponse res, String rawToken) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, rawToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days, matches REFRESH_TOKEN_DAYS
        cookie.setAttribute("SameSite", "Lax");
        res.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }

    private String readRefreshCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (REFRESH_COOKIE.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private String deviceInfo(HttpServletRequest req) {
        String ua = req.getHeader("User-Agent");
        return ua != null && ua.length() > 255 ? ua.substring(0, 255) : ua;
    }

    private String ip(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : req.getRemoteAddr();
    }
}