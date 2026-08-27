package com.revbook.authservice.controller;

import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.dto.UserResponse;
import com.revbook.authservice.service.AuthService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    private static final String COOKIE_NOME = "revbook_token";

    private final AuthService authService;
    private final long expirationMinutes;
    private final String cookieDomain;
    private final boolean cookieSecure;

    public UsuarioController(
            AuthService authService,
            @Value("${revbook.jwt.expiration-minutes}") long expirationMinutes,
            @Value("${revbook.cookie.domain}") String cookieDomain,
            @Value("${revbook.cookie.secure}") boolean cookieSecure) {
        this.authService = authService;
        this.expirationMinutes = expirationMinutes;
        this.cookieDomain = cookieDomain;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/auth/google")
    public ResponseEntity<UserResponse> loginComGoogle(@RequestBody GoogleLoginRequest dados) {
        LoginResponse resultado = authService.loginComGoogle(dados);
        ResponseCookie cookie = construirCookie(resultado.accessToken(), Duration.ofMinutes(expirationMinutes));
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resultado.user());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = construirCookie("", Duration.ZERO);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    private ResponseCookie construirCookie(String valor, Duration duracao) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(COOKIE_NOME, valor)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(duracao);
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }
}
