package com.revbook.authservice.dto;

/**
 * Espelha o payload que o Gateway repassa de {@code POST /auth/google} sem transformação,
 * assim como o auth-service original em NestJS. O frontend hoje envia { token }, então este
 * contrato só é preenchido quando algo decodifica o ID token do Google antes de chamar aqui.
 */
public record GoogleLoginRequest(
        String email,
        String firstName,
        String lastName,
        String picture,
        String id) {
}
