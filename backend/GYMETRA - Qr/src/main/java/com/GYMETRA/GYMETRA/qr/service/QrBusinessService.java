package com.GYMETRA.GYMETRA.qr.service;

import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import com.GYMETRA.GYMETRA.qr.repository.QrAccessRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrBusinessService {
    private final QrAccessRepository qrAccessRepository;
    private final RestTemplate restTemplate;

    public QrBusinessService(QrAccessRepository qrAccessRepository) {
        this.qrAccessRepository = qrAccessRepository;
        this.restTemplate = new RestTemplate();
    }

    public QrAccess getOrCreateQrForUser(Long userId) {
        Optional<QrAccess> existing = qrAccessRepository.findFirstByUserIdAndStatus(userId, "active");
        if (existing.isPresent()) {
            QrAccess qr = existing.get();
            // Validar cada 12 horas
            if (qr.getGeneratedAt() == null || qr.getGeneratedAt().isBefore(LocalDateTime.now().minusHours(12))) {
                updateQrStatusByMembership(qr);
            }
            return qr;
        } else {
            QrAccess newQr = new QrAccess();
            newQr.setUserId(userId);
            newQr.setQrCode(generateQrCode(userId));
            newQr.setGeneratedAt(LocalDateTime.now());
            // Consultar membresía y setear status
            String status = getMembershipStatus(userId) ? "active" : "inactive";
            newQr.setStatus(status);
            return qrAccessRepository.save(newQr);
        }
    }

    private void updateQrStatusByMembership(QrAccess qr) {
        boolean active = getMembershipStatus(qr.getUserId());
        qr.setStatus(active ? "active" : "inactive");
        qr.setGeneratedAt(LocalDateTime.now());
        qrAccessRepository.save(qr);
    }

    private boolean getMembershipStatus(Long userId) {
        String url = "http://localhost:8081/api/user-memberships/user/" + userId;
        try {
            Object[] response = restTemplate.getForObject(url, Object[].class);
            if (response != null) {
                for (Object obj : response) {
                    if (obj instanceof java.util.Map map) {
                        Object status = map.get("status");
                        if (status != null && "ACTIVE".equalsIgnoreCase(status.toString())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // log error
        }
        return false;
    }

    private String generateQrCode(Long userId) {
        // Genera un string base64 simple, puedes cambiar por lógica real de QR
        return java.util.Base64.getEncoder().encodeToString((userId + ":" + UUID.randomUUID()).getBytes());
    }
}
