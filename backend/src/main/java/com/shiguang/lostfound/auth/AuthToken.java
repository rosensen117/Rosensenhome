package com.shiguang.lostfound.auth;

import java.time.Instant;

public class AuthToken {
    private Long id;
    private Long userId;
    private String tokenHash;
    private Instant expiresAt;
    private Instant createdAt = Instant.now();

    public AuthToken() {}

    public AuthToken(UserAccount user, String tokenHash, Instant expiresAt) {
        this.userId = user.getId();
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
