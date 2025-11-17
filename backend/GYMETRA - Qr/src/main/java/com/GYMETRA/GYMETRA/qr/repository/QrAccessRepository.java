package com.GYMETRA.GYMETRA.qr.repository;

import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface QrAccessRepository extends JpaRepository<QrAccess, Long> {
    Optional<QrAccess> findFirstByUserIdAndStatus(Long userId, String status);
    List<QrAccess> findByUserId(Long userId);
}