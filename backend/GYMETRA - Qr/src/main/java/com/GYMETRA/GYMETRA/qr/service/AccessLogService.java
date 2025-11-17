package com.GYMETRA.GYMETRA.qr.service;

import com.GYMETRA.GYMETRA.qr.entity.AccessLog;
import com.GYMETRA.GYMETRA.qr.repository.AccessLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccessLogService {
    private final AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll();
    }

    public AccessLog save(AccessLog log) {
        return accessLogRepository.save(log);
    }
}