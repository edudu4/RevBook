package com.revbook.reviewservice.security;

public record UsuarioAutenticado(Long id, String email, String nome, String avatar) {
}
