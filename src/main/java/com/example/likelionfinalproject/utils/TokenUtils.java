package com.example.likelionfinalproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class TokenUtils {

    private static final long expirationTimeMs = 1000 * 60 * 60;

    public static String createToken(String userId, String key) {
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setSubject(userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    public static boolean isExpired(String token, String key) {
        return extractClaims(token, key).getExpiration().before(new Date());
    }

    public static String getUserId(String token, String key) {
        return extractClaims(token, key).getSubject();
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String userId) {
        return new UsernamePasswordAuthenticationToken(userId,
                null,
                List.of(new SimpleGrantedAuthority("USER")));
    }
}
