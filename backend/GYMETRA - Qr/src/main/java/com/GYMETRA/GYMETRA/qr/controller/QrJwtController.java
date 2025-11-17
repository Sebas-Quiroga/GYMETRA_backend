package com.GYMETRA.GYMETRA.qr.controller;

import com.GYMETRA.GYMETRA.qr.entity.QrAccess;
import com.GYMETRA.GYMETRA.qr.service.QrBusinessService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@RestController
@RequestMapping("/api/qr")
public class QrJwtController {
    private final QrBusinessService qrBusinessService;

    @Value("${jwt.secret:secret}")
    private String jwtSecret;

    public QrJwtController(QrBusinessService qrBusinessService) {
        this.qrBusinessService = qrBusinessService;
    }

    @GetMapping("/me")
    public ResponseEntity<QrAccess> getQrFromJwt(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        QrAccess qr = qrBusinessService.getOrCreateQrForUser(userId);
        return ResponseEntity.ok(qr);
    }

    private Long extractUserIdFromToken(String token) {
        try {
            Key key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Object id = claims.get("id");
            if (id instanceof Integer) return ((Integer) id).longValue();
            if (id instanceof Long) return (Long) id;
            if (id instanceof String) return Long.parseLong((String) id);
        } catch (Exception e) {
            // log error
        }
        return null;
    }
}
