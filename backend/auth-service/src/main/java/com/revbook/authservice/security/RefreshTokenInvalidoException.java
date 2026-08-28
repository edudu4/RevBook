package com.revbook.authservice.security;

public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
