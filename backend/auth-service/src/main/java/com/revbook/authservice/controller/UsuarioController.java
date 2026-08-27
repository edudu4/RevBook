package com.revbook.authservice.controller;

import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fala diretamente o contrato público — o Gateway (Spring Cloud Gateway) só roteia
 * /auth/** para cá e valida o JWT nas rotas protegidas, sem transformar payload.
 */
@RestController
public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/google")
    public LoginResponse loginComGoogle(@RequestBody GoogleLoginRequest dados) {
        return authService.loginComGoogle(dados);
    }
}
