package tn.esprit._4se2.pi.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // 🔑 Génère un token JWT après connexion
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, null);
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_PLAYER");

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        if (userId != null) {
            claims.put("userId", userId);
        }

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    // 📧 Extrait l'email depuis le token
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object rawUserId = parseClaims(token).get("userId");

        if (rawUserId instanceof Number number) {
            return number.longValue();
        }

        if (rawUserId instanceof String userIdValue && !userIdValue.isBlank()) {
            try {
                return Long.parseLong(userIdValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    // ✅ Vérifie que le token est valide
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    // 🔍 Parse et extrait les claims du token
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
