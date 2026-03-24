package com.velkas.wishlist.security;

import com.velkas.wishlist.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String USERNAME_CLAIM = "username";

    private final JwtProperties jwtProperties;

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpiration());

        var builder = Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey());

        if (StringUtils.hasText(username)) {
            builder.claim(USERNAME_CLAIM, username);
        }

        return builder.compact();
    }

    public JwtUserClaims parseToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return new JwtUserClaims(
            Long.parseLong(claims.getSubject()),
            claims.get(USERNAME_CLAIM, String.class)
        );
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
