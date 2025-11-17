package com.login.GYMETRA.repository;

import com.login.GYMETRA.entity.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= :now")
    int deleteAllExpiredSince(LocalDateTime now);

    @Transactional
    @Modifying
    @Query("delete from PasswordResetToken t where t.user.id = :userId")
    int deleteAllByUserId(Long userId);
}

