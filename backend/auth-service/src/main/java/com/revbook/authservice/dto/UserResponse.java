package com.revbook.authservice.dto;

import com.revbook.authservice.domain.Usuario;

public record UserResponse(Long id, String email, String name, String avatar) {

    public static UserResponse de(Usuario usuario) {
        return new UserResponse(usuario.getId(), usuario.getEmail(), usuario.getNome(), usuario.getAvatar());
    }
}
