package com.revbook.reviewservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Mesma chave HMAC do auth-service e do Gateway — só valida, não emite tokens aqui. */
@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${revbook.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public UsuarioAutenticado validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new UsuarioAutenticado(
                    Long.valueOf(claims.getSubject()),
                    claims.get("email", String.class),
                    claims.get("name", String.class));
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }
}
