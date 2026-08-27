package com.revbook.authservice.service;

import com.revbook.authservice.domain.Usuario;
import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.dto.UserResponse;
import com.revbook.authservice.repository.UsuarioRepository;
import com.revbook.authservice.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse loginComGoogle(GoogleLoginRequest dados) {
        Usuario usuario = usuarioRepository.findByEmail(dados.email())
                .orElseGet(() -> usuarioRepository.save(new Usuario(
                        dados.email(),
                        (dados.firstName() + " " + dados.lastName()).trim(),
                        dados.id(),
                        dados.picture())));

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, UserResponse.de(usuario));
    }
}
