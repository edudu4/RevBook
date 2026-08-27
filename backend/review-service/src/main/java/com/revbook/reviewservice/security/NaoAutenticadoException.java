package com.revbook.reviewservice.security;

/** O Gateway já bloqueia chamadas sem token nas rotas protegidas — isto é a segunda camada (defesa em profundidade). */
public class NaoAutenticadoException extends RuntimeException {

    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
