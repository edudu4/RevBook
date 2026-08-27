package com.revbook.reviewservice.exception;

/** Lançada quando o googleBooksId informado não corresponde a um livro real na Google Books API. */
public class LivroInvalidoException extends RuntimeException {

    public LivroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
