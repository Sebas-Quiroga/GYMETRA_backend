package com.GYMETRA.GYMETRA.qr.controller;

import com.GYMETRA.GYMETRA.qr.entity.AccessLog;
import com.GYMETRA.GYMETRA.qr.service.AccessLogService;
import com.GYMETRA.GYMETRA.qr.service.AccessLogBusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/access-log")
public class AccessLogController {
    private final AccessLogService accessLogService;
    private final AccessLogBusinessService accessLogBusinessService;

    public AccessLogController(AccessLogService accessLogService, AccessLogBusinessService accessLogBusinessService) {
        this.accessLogService = accessLogService;
        this.accessLogBusinessService = accessLogBusinessService;
    }

    @GetMapping
    public List<AccessLog> getAllLogs() {
        return accessLogService.getAllLogs();
    }



    @PostMapping("/entrada")
    public ResponseEntity<?> marcarEntrada(@RequestBody EntradaSalidaRequest req) {
        try {
            AccessLog log = accessLogBusinessService.marcarIngreso(req.getUserId(), req.getBranchId());
            return ResponseEntity.ok(new AccessLogResponse(log));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/salida")
    public ResponseEntity<?> marcarSalida(@RequestBody EntradaSalidaRequest req) {
        try {
            AccessLog log = accessLogBusinessService.marcarSalida(req.getUserId(), req.getBranchId());
            return ResponseEntity.ok(new AccessLogResponse(log));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Response DTO with duration in hours
    public static class AccessLogResponse {
        private Long logId;
        private Long userId;
        private Long qrId;
        private Long branchId;
        private String result;
        private String notes;
        private java.time.LocalDateTime eventTs;
        private java.time.LocalDateTime entryTime;
        private java.time.LocalDateTime exitTime;
        private Long durationInHours;

        public AccessLogResponse(AccessLog log) {
            this.logId = log.getLogId();
            this.userId = log.getUserId();
            this.qrId = log.getQrId();
            this.branchId = log.getBranchId();
            this.result = log.getResult();
            this.notes = log.getNotes();
            this.eventTs = log.getEventTs();
            this.entryTime = log.getEntryTime();
            this.exitTime = log.getExitTime();
            this.durationInHours = log.getDurationInHours();
        }

        public Long getLogId() { return logId; }
        public Long getUserId() { return userId; }
        public Long getQrId() { return qrId; }
        public Long getBranchId() { return branchId; }
        public String getResult() { return result; }
        public String getNotes() { return notes; }
        public java.time.LocalDateTime getEventTs() { return eventTs; }
        public java.time.LocalDateTime getEntryTime() { return entryTime; }
        public java.time.LocalDateTime getExitTime() { return exitTime; }
        public Long getDurationInHours() { return durationInHours; }
    }

    public static class EntradaSalidaRequest {
        private Long userId;
        private Long branchId;
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getBranchId() { return branchId; }
        public void setBranchId(Long branchId) { this.branchId = branchId; }
    }
}