package com.example.leave_management;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "your_secret_key_123456";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    public String generateToken(String username, String role, String userId, Long roleId) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withClaim("userId", userId)
                .withClaim("roleId", roleId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        return verifier.verify(token);
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    public String extractRole(String token) {
        return decodeToken(token).getClaim("role").asString();
    }

    public boolean isTokenExpired(String token) {
        return decodeToken(token).getExpiresAt().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUserId(String token) {
        return decodeToken(token).getClaim("userId").asString();
    }

    public Long extractRoleId(String token) {
        return decodeToken(token).getClaim("roleId").asLong();
    }
} 