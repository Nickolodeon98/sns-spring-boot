package com.example.likelionfinalproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class TokenUtils {

    private static final long expirationTimeMs = 1000 * 60 * 60;

    public static String createToken(String userId) {
        Date now = new Date();
        Claims claims = Jwts.claims()
                .setSubject(userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTimeMs))
                .compact();
    }
}