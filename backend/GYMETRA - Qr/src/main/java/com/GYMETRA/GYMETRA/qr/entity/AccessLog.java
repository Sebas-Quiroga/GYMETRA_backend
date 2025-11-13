package com.GYMETRA.GYMETRA.qr.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_log")
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "qr_id")
    private Long qrId;
    @Column(name = "branch_id")
    private Long branchId;
    @Column(name = "event_ts")
    private LocalDateTime eventTs;
    @Column(name = "entry_time")
    private LocalDateTime entryTime;
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
    @Column(name = "result")
    private String result; // granted, denied
    @Column(name = "notes")
    private String notes;

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getQrId() { return qrId; }
    public void setQrId(Long qrId) { this.qrId = qrId; }
    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public LocalDateTime getEventTs() { return eventTs; }
    public void setEventTs(LocalDateTime eventTs) { this.eventTs = eventTs; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Transient
    public Long getDurationInHours() {
        if (entryTime != null && exitTime != null) {
            return java.time.Duration.between(entryTime, exitTime).toHours();
        }
        return null;
    }
}