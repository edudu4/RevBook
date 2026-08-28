package com.revbook.authservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.authservice.domain.RefreshToken;
import com.revbook.authservice.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, 30);
    }

    @Test
    void gerar_deveSalvarERetornarUmTokenNaoVazio() {
        String token = refreshTokenService.gerar(1L);

        assertThat(token).isNotBlank();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void validarERotacionar_deveRetornarUsuarioIdERemoverToken_quandoValido() {
        RefreshToken token = new RefreshToken("token-valido", 42L, LocalDateTime.now().plusDays(1));
        when(refreshTokenRepository.findByToken("token-valido")).thenReturn(Optional.of(token));

        Long usuarioId = refreshTokenService.validarERotacionar("token-valido");

        assertThat(usuarioId).isEqualTo(42L);
        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void validarERotacionar_deveLancarExcecao_quandoTokenNaoExiste() {
        when(refreshTokenRepository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validarERotacionar("token-inexistente"))
                .isInstanceOf(RefreshTokenInvalidoException.class);
    }

    @Test
    void validarERotacionar_deveLancarExcecaoERemoverToken_quandoExpirado() {
        RefreshToken token = new RefreshToken("token-expirado", 42L, LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.validarERotacionar("token-expirado"))
                .isInstanceOf(RefreshTokenInvalidoException.class);

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void revogar_deveDelegarParaORepositorio() {
        refreshTokenService.revogar("token-qualquer");

        verify(refreshTokenRepository).deleteByToken("token-qualquer");
    }

    @Test
    void revogar_naoDeveFalhar_quandoTokenNaoExiste() {
        refreshTokenService.revogar("token-inexistente");

        verify(refreshTokenRepository, never()).findByToken(any());
    }
}
