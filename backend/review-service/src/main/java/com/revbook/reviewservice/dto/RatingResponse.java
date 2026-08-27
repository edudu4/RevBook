package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Avaliacao;

public record RatingResponse(Long id, Long userId, Integer value) {

    public static RatingResponse de(Avaliacao avaliacao) {
        return new RatingResponse(avaliacao.getId(), avaliacao.getUsuarioId(), avaliacao.getValor());
    }
}
