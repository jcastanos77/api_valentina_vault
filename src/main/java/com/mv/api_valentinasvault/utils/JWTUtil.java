package com.mv.api_valentinasvault.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.mv.api_valentinasvault.model.User;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTUtil {

    // 🔐 Usa una clave segura (mínimo 32 caracteres para HS256)
    private static final String SECRET_KEY = "qWbSlg1d6gUPzV9bAvhXb5l4R64tLrjY3GfQ1uR02Zc=";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 día

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // ✅ aquí corregido
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // ✅ unificada
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }
}
