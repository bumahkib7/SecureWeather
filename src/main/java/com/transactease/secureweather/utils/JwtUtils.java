package com.transactease.secureweather.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalArgumentException("JWT secret key not configured. Please provide a non-empty value for 'app.jwtSecret' property.");
        }
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(@NotNull Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Get the user roles from the UserDetails object
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Convert the authorities to a comma-separated string of role values
        String roles = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Set the expiration time based on jwtExpirationMs
        Date expiration = new Date(nowMillis + jwtExpirationMs);

        JwtBuilder jwtBuilder = Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("roles", roles) // Add the roles as a custom claim
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(secretKey);

        return jwtBuilder.compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
