package com.GYMETRA.GYMETRA.qr.service;

import com.GYMETRA.GYMETRA.qr.entity.AccessLog;
import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import com.GYMETRA.GYMETRA.qr.repository.AccessLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class AccessLogBusinessService {
    private final AccessLogRepository accessLogRepository;
    // Removed unused field qrAccessRepository
    private final QrBusinessService qrBusinessService;

    public AccessLogBusinessService(AccessLogRepository accessLogRepository, QrBusinessService qrBusinessService) {
        this.accessLogRepository = accessLogRepository;
        this.qrBusinessService = qrBusinessService;
    }

    public AccessLog marcarIngreso(Long userId, Long branchId) {
        QrAccess qr = qrBusinessService.getOrCreateQrForUser(userId);
        if (!"active".equalsIgnoreCase(qr.getStatus())) {
            throw new RuntimeException("User does not have an active membership");
        }
        // No permitir doble entrada en la misma jornada (sin salida)
        List<AccessLog> logs = accessLogRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        boolean hasOpenEntry = logs.stream().anyMatch(l ->
            l.getUserId().equals(userId)
            && l.getBranchId().equals(branchId)
            && l.getExitTime() == null
            && isSameShift(l.getEntryTime(), now)
        );
        if (hasOpenEntry) {
            throw new RuntimeException("User already has an open entry for this shift");
        }
        AccessLog log = new AccessLog();
        log.setUserId(userId);
        log.setQrId(qr.getQrId());
        log.setBranchId(branchId);
        log.setEntryTime(now);
        log.setEventTs(now);
        log.setResult("granted");
        return accessLogRepository.save(log);
    }

    // Shift: 6:00-12:00, 12:01-22:00
    private boolean isSameShift(LocalDateTime t1, LocalDateTime t2) {
        if (t1 == null || t2 == null) return false;
        int h1 = t1.getHour(), h2 = t2.getHour();
        if (h1 >= 6 && h1 <= 12 && h2 >= 6 && h2 <= 12) return t1.toLocalDate().equals(t2.toLocalDate());
        if (h1 >= 12 && h1 <= 22 && h2 >= 12 && h2 <= 22) return t1.toLocalDate().equals(t2.toLocalDate());
        return false;
    }

    public AccessLog marcarSalida(Long userId, Long branchId) {
        Optional<AccessLog> logOpt = accessLogRepository.findAll().stream()
            .filter(l -> l.getUserId().equals(userId) && l.getExitTime() == null && (branchId == null || branchId.equals(l.getBranchId())))
            .findFirst();
        if (logOpt.isPresent()) {
            AccessLog log = logOpt.get();
            log.setExitTime(LocalDateTime.now());
            return accessLogRepository.save(log);
        } else {
            throw new RuntimeException("No open entry found for this user and branch");
        }
    }
}
