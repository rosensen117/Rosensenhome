package com.shiguang.lostfound.auth;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_student_id", columnList = "studentId", unique = true),
        @Index(name = "idx_users_phone", columnList = "phone", unique = true)
})
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String studentId;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected UserAccount() {}

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
}
