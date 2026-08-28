package com.revbook.authservice.controller;

import com.revbook.authservice.dto.GoogleLoginRequest;
import com.revbook.authservice.dto.LoginResponse;
import com.revbook.authservice.dto.UserResponse;
import com.revbook.authservice.service.AuthService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    private static final String COOKIE_ACCESS = "revbook_token";
    private static final String COOKIE_REFRESH = "revbook_refresh";

    private final AuthService authService;
    private final long expirationMinutes;
    private final long refreshExpirationDays;
    private final String cookieDomain;
    private final boolean cookieSecure;

    public UsuarioController(
            AuthService authService,
            @Value("${revbook.jwt.expiration-minutes}") long expirationMinutes,
            @Value("${revbook.refresh.expiration-days}") long refreshExpirationDays,
            @Value("${revbook.cookie.domain}") String cookieDomain,
            @Value("${revbook.cookie.secure}") boolean cookieSecure) {
        this.authService = authService;
        this.expirationMinutes = expirationMinutes;
        this.refreshExpirationDays = refreshExpirationDays;
        this.cookieDomain = cookieDomain;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/auth/google")
    public ResponseEntity<UserResponse> loginComGoogle(@RequestBody GoogleLoginRequest dados) {
        LoginResponse resultado = authService.loginComGoogle(dados);
        return responderComCookies(resultado);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<UserResponse> renovar(
            @CookieValue(name = COOKIE_REFRESH, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LoginResponse resultado = authService.renovar(refreshToken);
        return responderComCookies(resultado);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = COOKIE_REFRESH, required = false) String refreshToken) {
        authService.logout(refreshToken);

        ResponseCookie access = construirCookie(COOKIE_ACCESS, "", "/", Duration.ZERO);
        ResponseCookie refresh = construirCookie(COOKIE_REFRESH, "", "/auth", Duration.ZERO);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, access.toString())
                .header(HttpHeaders.SET_COOKIE, refresh.toString())
                .build();
    }

    private ResponseEntity<UserResponse> responderComCookies(LoginResponse resultado) {
        ResponseCookie access = construirCookie(
                COOKIE_ACCESS, resultado.accessToken(), "/", Duration.ofMinutes(expirationMinutes));
        ResponseCookie refresh = construirCookie(
                COOKIE_REFRESH, resultado.refreshToken(), "/auth", Duration.ofDays(refreshExpirationDays));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, access.toString())
                .header(HttpHeaders.SET_COOKIE, refresh.toString())
                .body(resultado.user());
    }

    private ResponseCookie construirCookie(String nome, String valor, String path, Duration duracao) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(nome, valor)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(path)
                .maxAge(duracao);
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }
}
