package com.revbook.authservice.security;

import com.revbook.authservice.domain.RefreshToken;
import com.revbook.authservice.repository.RefreshTokenRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long expirationDays;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${revbook.refresh.expiration-days}") long expirationDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.expirationDays = expirationDays;
    }

    public long getExpirationDays() {
        return expirationDays;
    }

    public String gerar(Long usuarioId) {
        String token = gerarTokenAleatorio();
        LocalDateTime expiraEm = LocalDateTime.now().plusDays(expirationDays);
        refreshTokenRepository.save(new RefreshToken(token, usuarioId, expiraEm));
        return token;
    }

    public Long validarERotacionar(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token inválido"));

        refreshTokenRepository.delete(refreshToken);

        if (refreshToken.expirado()) {
            throw new RefreshTokenInvalidoException("Refresh token expirado");
        }

        return refreshToken.getUsuarioId();
    }

    public void revogar(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    private String gerarTokenAleatorio() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
