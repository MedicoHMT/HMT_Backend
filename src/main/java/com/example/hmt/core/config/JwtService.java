package com.example.hmt.core.config;

import com.example.hmt.core.auth.model.Role;
import com.example.hmt.core.auth.model.User;
import com.example.hmt.core.tenant.Hospital;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateTokenSuperAdmin(String email, Role role) {
        return Jwts
                .builder()
                .subject(email)
                .claim("role", role != null ? role.name() : null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractHospitalId(String token) {
        return extractAllClaims(token).get("hospitalId", Long.class);
    }

    public UUID extractUserId(String token) {
        Object raw = extractAllClaims(token).get("userId");
        if (raw == null) return null;
        if (raw instanceof UUID) return (UUID) raw;
        try {
            return UUID.fromString(raw.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String extractRole(String token) {
        Object raw = extractAllClaims(token).get("role");
        return raw == null ? null : raw.toString();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getBody();
    }



    public String generateToken(User user) {


        Hospital hospital = user.getHospital();

        Long hospitalId = (hospital != null) ? hospital.getId() : null;
        String hospitalName = (hospital != null) ? hospital.getName() : null;

        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId() != null ? user.getId().toString() : null)
                .claim("hospitalId", hospitalId)
                .claim("hospitalName", hospitalName)
                .claim("permissions", user.getPermissions())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    // Validate token signature / expiration without requiring a User
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }
}
