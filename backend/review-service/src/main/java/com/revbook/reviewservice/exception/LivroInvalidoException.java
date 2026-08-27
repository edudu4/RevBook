package com.revbook.reviewservice.exception;

public class LivroInvalidoException extends RuntimeException {

    public LivroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
