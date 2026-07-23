package com.shiguang.lostfound.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByStudentId(String studentId);
    Optional<UserAccount> findByStudentIdOrPhone(String studentId, String phone);
    boolean existsByStudentId(String studentId);
    boolean existsByPhone(String phone);
}
