package com.revbook.authservice.service;

import com.revbook.authservice.domain.Usuario;
import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.dto.UserResponse;
import com.revbook.authservice.repository.UsuarioRepository;
import com.revbook.authservice.security.GoogleTokenService;
import com.revbook.authservice.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final GoogleTokenService googleTokenService;

    public AuthService(
            UsuarioRepository usuarioRepository, JwtService jwtService, GoogleTokenService googleTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.googleTokenService = googleTokenService;
    }

    public LoginResponse loginComGoogle(GoogleLoginRequest dados) {
        GoogleTokenService.DadosUsuarioGoogle dadosGoogle = googleTokenService.verificar(dados.token());

        Usuario usuario = usuarioRepository.findByEmail(dadosGoogle.email())
                .orElseGet(() -> usuarioRepository.save(new Usuario(
                        dadosGoogle.email(), dadosGoogle.nome(), dadosGoogle.googleId(), dadosGoogle.avatar())));

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, UserResponse.de(usuario));
    }
}
