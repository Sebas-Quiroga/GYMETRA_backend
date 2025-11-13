package com.GYMETRA.GYMETRA.qr.repository;

import com.GYMETRA.GYMETRA.qr.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}