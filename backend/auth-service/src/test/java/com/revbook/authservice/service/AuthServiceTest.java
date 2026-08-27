package com.revbook.authservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.revbook.authservice.domain.Usuario;
import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.repository.UsuarioRepository;
import com.revbook.authservice.security.GoogleTokenService;
import com.revbook.authservice.security.JwtService;
import com.revbook.authservice.security.TokenGoogleInvalidoException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private GoogleTokenService googleTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginComGoogle_deveCriarNovoUsuario_quandoEmailNaoExiste() {
        var dadosGoogle = new GoogleTokenService.DadosUsuarioGoogle(
                "google-1", "nova@example.com", "Nova Pessoa", "https://avatar/1");
        when(googleTokenService.verificar("token-valido")).thenReturn(dadosGoogle);
        when(usuarioRepository.findByEmail("nova@example.com")).thenReturn(Optional.empty());

        Usuario usuarioSalvo = new Usuario("nova@example.com", "Nova Pessoa", "google-1", "https://avatar/1");
        ReflectionTestUtils.setField(usuarioSalvo, "id", 10L);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);
        when(jwtService.gerarToken(usuarioSalvo)).thenReturn("jwt-gerado");

        LoginResponse resposta = authService.loginComGoogle(new GoogleLoginRequest("token-valido"));

        assertThat(resposta.accessToken()).isEqualTo("jwt-gerado");
        assertThat(resposta.user().id()).isEqualTo(10L);
        assertThat(resposta.user().email()).isEqualTo("nova@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void loginComGoogle_deveReaproveitarUsuarioExistente_quandoEmailJaExiste() {
        var dadosGoogle = new GoogleTokenService.DadosUsuarioGoogle(
                "google-2", "existente@example.com", "Pessoa Existente", "https://avatar/2");
        when(googleTokenService.verificar("token-valido")).thenReturn(dadosGoogle);

        Usuario usuarioExistente =
                new Usuario("existente@example.com", "Pessoa Existente", "google-2", "https://avatar/2");
        ReflectionTestUtils.setField(usuarioExistente, "id", 5L);
        when(usuarioRepository.findByEmail("existente@example.com")).thenReturn(Optional.of(usuarioExistente));
        when(jwtService.gerarToken(usuarioExistente)).thenReturn("jwt-existente");

        LoginResponse resposta = authService.loginComGoogle(new GoogleLoginRequest("token-valido"));

        assertThat(resposta.accessToken()).isEqualTo("jwt-existente");
        assertThat(resposta.user().id()).isEqualTo(5L);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginComGoogle_devePropagarExcecao_quandoTokenGoogleInvalido() {
        when(googleTokenService.verificar("token-invalido"))
                .thenThrow(new TokenGoogleInvalidoException("Token do Google inválido ou expirado"));

        assertThatThrownBy(() -> authService.loginComGoogle(new GoogleLoginRequest("token-invalido")))
                .isInstanceOf(TokenGoogleInvalidoException.class);

        verifyNoInteractions(usuarioRepository, jwtService);
    }
}
