package com.revbook.authservice.service;

import com.revbook.authservice.domain.Usuario;
import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.dto.UserResponse;
import com.revbook.authservice.repository.UsuarioRepository;
import com.revbook.authservice.security.GoogleTokenService;
import com.revbook.authservice.security.JwtService;
import com.revbook.authservice.security.RefreshTokenInvalidoException;
import com.revbook.authservice.security.RefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final GoogleTokenService googleTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            GoogleTokenService googleTokenService,
            RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.googleTokenService = googleTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse loginComGoogle(GoogleLoginRequest dados) {
        GoogleTokenService.DadosUsuarioGoogle dadosGoogle = googleTokenService.verificar(dados.token());

        Usuario usuario = usuarioRepository.findByEmail(dadosGoogle.email())
                .orElseGet(() -> usuarioRepository.save(new Usuario(
                        dadosGoogle.email(), dadosGoogle.nome(), dadosGoogle.googleId(), dadosGoogle.avatar())));

        return gerarResposta(usuario);
    }

    public LoginResponse renovar(String refreshTokenAntigo) {
        Long usuarioId = refreshTokenService.validarERotacionar(refreshTokenAntigo);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RefreshTokenInvalidoException("Usuário não encontrado"));

        return gerarResposta(usuario);
    }

    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revogar(refreshToken);
        }
    }

    private LoginResponse gerarResposta(Usuario usuario) {
        String accessToken = jwtService.gerarToken(usuario);
        String refreshToken = refreshTokenService.gerar(usuario.getId());
        return new LoginResponse(accessToken, refreshToken, UserResponse.de(usuario));
    }
}
