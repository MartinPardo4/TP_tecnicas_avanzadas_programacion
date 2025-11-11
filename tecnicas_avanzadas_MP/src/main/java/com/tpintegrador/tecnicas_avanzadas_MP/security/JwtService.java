package com.tpintegrador.tecnicas_avanzadas_MP.security;

import com.tpintegrador.tecnicas_avanzadas_MP.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = Map.of(
                "id", usuario.getId(),
                "rol", usuario.getRol().name()
        );

        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusMillis(expirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(Date.from(ahora))
                .setExpiration(Date.from(expiracion))
                .signWith(getSigningKey())
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        String email = extraerEmail(token);
        return email.equals(userDetails.getUsername()) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        Date expiration = extraerClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSigningKey() {
        byte[] keyBytes;
        if (secret.length() % 4 == 0 && secret.matches("^[A-Za-z0-9+/=]*$")) {
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = Arrays.copyOf(digest.digest(secret.getBytes(StandardCharsets.UTF_8)), 32);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("No se pudo inicializar el algoritmo de firma JWT", e);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

