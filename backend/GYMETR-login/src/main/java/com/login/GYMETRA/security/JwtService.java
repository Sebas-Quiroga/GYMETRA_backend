package com.login.GYMETRA.security;

import com.login.GYMETRA.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // Clave secreta para firmar el token (mínimo 32 bytes)
    private final String SECRET_KEY = "mi+clave+secreta+super+segura+hipermegafantastica";

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ===============================
    // GENERACIÓN DE TOKEN
    // ===============================
    public String generateToken(Map<String, Object> extraClaims, User user) {
        Map<String, Object> claims = new HashMap<>(extraClaims);

        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("status", user.getStatus());
        claims.put("phone", user.getPhone());
        claims.put("identification", user.getIdentification()); // OJO: PII
        claims.put("photoUrl", user.getPhotoUrl());
        claims.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        claims.put("lastLogin", user.getLastLogin() != null ? user.getLastLogin().toString() : null);
        claims.put("roleIds", user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleId())
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===============================
    // MÉTODOS PARA EXTRAER INFORMACIÓN
    // ===============================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object id = claims.get("userId");
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    public List<Long> extractRoleIds(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roleIds");
        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .filter(Objects::nonNull)
                    .map(o -> Long.parseLong(o.toString()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===============================
    // VALIDACIÓN DE TOKEN
    // ===============================
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
