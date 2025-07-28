package com.example.bankcards.service.jwt;

import com.example.bankcards.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final JwtConfig jwtConfig;

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getAccessSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature or malformed token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(UserDetails user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getAccess_expiration() * 1000L))
                .signWith(getAccessSecretKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getRefresh_expiration() * 1000L))
                .signWith(getRefreshSecretKey())
                .compact();
    }

    private SecretKey getAccessSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getAccess_secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getRefreshSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getRefresh_secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
