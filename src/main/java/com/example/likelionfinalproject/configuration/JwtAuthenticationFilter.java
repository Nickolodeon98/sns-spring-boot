package com.example.likelionfinalproject.configuration;

import com.example.likelionfinalproject.service.UserService;
import com.example.likelionfinalproject.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    String secretKey;

    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.split(" ")[1];

        try {
            if (TokenUtils.isExpired(token, secretKey)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = TokenUtils.getUserId(token, secretKey);

            SecurityContextHolder.getContext().setAuthentication(TokenUtils.getAuthentication(userId));
        } catch (ExpiredJwtException e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
        filterChain.doFilter(request, response);
    }
}
