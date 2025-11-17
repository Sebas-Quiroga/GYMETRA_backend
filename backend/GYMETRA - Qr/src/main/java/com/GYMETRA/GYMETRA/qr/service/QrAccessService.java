package com.GYMETRA.GYMETRA.qr.service;

import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import com.GYMETRA.GYMETRA.qr.repository.QrAccessRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class QrAccessService {
    private final QrAccessRepository qrAccessRepository;

    public QrAccessService(QrAccessRepository qrAccessRepository) {
        this.qrAccessRepository = qrAccessRepository;
    }

    public Optional<QrAccess> getActiveQrByUserId(Long userId) {
        return qrAccessRepository.findFirstByUserIdAndStatus(userId, "active");
    }

    public QrAccess save(QrAccess qrAccess) {
        return qrAccessRepository.save(qrAccess);
    }

    public List<QrAccess> getAllByUserId(Long userId) {
        return qrAccessRepository.findByUserId(userId);
    }
}