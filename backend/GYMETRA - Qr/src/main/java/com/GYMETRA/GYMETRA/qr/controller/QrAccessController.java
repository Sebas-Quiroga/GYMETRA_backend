package com.GYMETRA.GYMETRA.qr.controller;

import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import com.GYMETRA.GYMETRA.qr.service.QrAccessService;
import com.GYMETRA.GYMETRA.qr.service.QrBusinessService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/qr-access")

public class QrAccessController {
    private final QrAccessService qrAccessService;
    private final QrBusinessService qrBusinessService;

    public QrAccessController(QrAccessService qrAccessService, QrBusinessService qrBusinessService) {
        this.qrAccessService = qrAccessService;
        this.qrBusinessService = qrBusinessService;
    }

    @GetMapping("/user/{userId}")
    public QrAccess getOrCreateQr(@PathVariable Long userId) {
        return qrBusinessService.getOrCreateQrForUser(userId);
    }

    @PostMapping
    public QrAccess createQr(@RequestBody QrAccess qrAccess) {
        return qrAccessService.save(qrAccess);
    }

    @GetMapping("/all/{userId}")
    public List<QrAccess> getAllByUser(@PathVariable Long userId) {
        return qrAccessService.getAllByUserId(userId);
    }
}