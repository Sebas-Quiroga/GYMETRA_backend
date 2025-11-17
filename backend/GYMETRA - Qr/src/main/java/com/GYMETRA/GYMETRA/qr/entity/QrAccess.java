package com.GYMETRA.GYMETRA.qr.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_access")
public class QrAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_id")
    private Long qrId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "qr_code", length = 1024)
    private String qrCode;
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    @Column(name = "status")
    private String status; // active, inactive, expired

    public Long getQrId() {
        return qrId;
    }
    public void setQrId(Long qrId) {
        this.qrId = qrId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}