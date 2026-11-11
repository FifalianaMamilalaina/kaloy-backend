package org.example.mozika.repositories;

import jakarta.persistence.LockModeType;
import org.example.mozika.models.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token from AuthRefreshToken token
            where token.tokenHash = :tokenHash and token.expiresAt > :now
            """)
    Optional<AuthRefreshToken> findValidForUpdate(
            @Param("tokenHash") String tokenHash,
            @Param("now") LocalDateTime now
    );

    void deleteByTokenHash(String tokenHash);
}
