package com.GYMETRA.GYMETRA.qr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/memberships-proxy")
public class MembershipProxyController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String MEMBERSHIP_API = "http://192.168.0.11:8081/api/user-memberships/user/";

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMembership(@PathVariable Long userId) {
        try {
            Object response = restTemplate.getForObject(MEMBERSHIP_API + userId, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(502).body("Error consultando membresía: " + e.getMessage());
        }
    }
}
