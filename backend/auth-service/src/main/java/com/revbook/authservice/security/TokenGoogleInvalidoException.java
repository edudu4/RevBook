package com.revbook.authservice.security;

public class TokenGoogleInvalidoException extends RuntimeException {

    public TokenGoogleInvalidoException(String mensagem) {
        super(mensagem);
    }
}
