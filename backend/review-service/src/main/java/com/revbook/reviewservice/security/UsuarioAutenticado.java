package com.revbook.reviewservice.security;

/** Identidade extraída do JWT relayado pelo Gateway. */
public record UsuarioAutenticado(Long id, String email, String nome) {
}
