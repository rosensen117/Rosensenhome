package com.shiguang.lostfound.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<AuthToken> findByTokenHash(String tokenHash);
    @Transactional
    void deleteByTokenHash(String tokenHash);
}
