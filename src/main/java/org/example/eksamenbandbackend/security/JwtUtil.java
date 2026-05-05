package org.example.eksamenbandbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 8; // 8 hours

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    private volatile SecretKey signingKey;

    private SecretKey getSigningKey() {
        SecretKey currentKey = signingKey;
        if (currentKey == null) {
            synchronized (this) {
                currentKey = signingKey;
                if (currentKey == null) {
                    byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
                    currentKey = Keys.hmacShaKeyFor(keyBytes);
                    signingKey = currentKey;
                }
            }
        }
        return currentKey;
    }

    // -----------------------------
    // Token Generation
    // -----------------------------
    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                // Attach any optional claims before setting the standard JWT fields.
                .claims().add(extraClaims).and() // modern replacement for setClaims()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // -----------------------------
    // Token Validation
    // -----------------------------
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // -----------------------------
    // Extracting Claims
    // -----------------------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        // Parse the signed token once, then project the specific claim the caller
        // needs.
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Verify the signature before exposing any token contents.
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
