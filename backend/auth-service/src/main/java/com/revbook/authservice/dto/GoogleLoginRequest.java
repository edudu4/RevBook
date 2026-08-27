package com.revbook.authservice.dto;

/** Espelha o payload que o front-end (Login.tsx) envia: o ID token bruto do Google Identity Services. */
public record GoogleLoginRequest(String token) {
}
