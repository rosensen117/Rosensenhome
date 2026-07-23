package com.shiguang.lostfound.auth;

import java.time.Instant;

public class UserAccount {
    private Long id;
    private String name;
    private String studentId;
    private String phone;
    private String email;
    private String passwordHash;
    private Role role = Role.USER;
    private boolean verified = false;
    private Instant createdAt = Instant.now();

    public UserAccount() {}

    public UserAccount(String name, String studentId, String phone, String email, String passwordHash, Role role) {
        this.name = name;
        this.studentId = studentId;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isVerified() { return verified; }
    public Instant getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
