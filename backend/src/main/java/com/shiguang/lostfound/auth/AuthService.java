package com.shiguang.lostfound.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.Base64;

import static com.shiguang.lostfound.auth.AuthDtos.*;

@Service
public class AuthService {
    private final UserAccountRepository users;
    private final AuthTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration tokenLifetime;

    public AuthService(UserAccountRepository users, AuthTokenRepository tokens, PasswordEncoder passwordEncoder,
                       @Value("${app.auth.token-hours:168}") long tokenHours) {
        this.users = users;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.tokenLifetime = Duration.ofHours(tokenHours);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String studentId = request.studentId().trim();
        String phone = request.phone().trim();
        if (users.existsByStudentId(studentId)) throw new ApiException(HttpStatus.CONFLICT, "该学号已注册");
        if (users.existsByPhone(phone)) throw new ApiException(HttpStatus.CONFLICT, "该手机号已注册");
        String email = request.email() == null || request.email().isBlank() ? null : request.email().trim();
        UserAccount user = users.save(new UserAccount(request.name().trim(), studentId, phone, email,
                passwordEncoder.encode(request.password()), Role.USER));
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, boolean adminOnly) {
        String account = request.account().trim();
        UserAccount user = users.findByStudentIdOrPhone(account, account)
                .orElseThrow(() -> invalidCredentials(adminOnly));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) throw invalidCredentials(adminOnly);
        if (adminOnly && user.getRole() != Role.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN, "该账号没有管理员权限");
        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String account) {
        return users.findByStudentId(account).map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效"));
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken != null && !rawToken.isBlank()) tokens.deleteByTokenHash(hash(rawToken));
    }

    @Transactional(readOnly = true)
    public UserAccount authenticate(String rawToken) {
        AuthToken token = tokens.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录"));
        if (token.getExpiresAt().isBefore(Instant.now())) throw new ApiException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
        return token.getUser();
    }

    private AuthResponse issueToken(UserAccount user) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.save(new AuthToken(user, hash(raw), Instant.now().plus(tokenLifetime)));
        return new AuthResponse(raw, tokenLifetime.toSeconds(), UserResponse.from(user));
    }

    private ApiException invalidCredentials(boolean adminOnly) {
        return new ApiException(HttpStatus.UNAUTHORIZED, adminOnly ? "管理员账号或密码错误" : "账号或密码错误");
    }

    public static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
