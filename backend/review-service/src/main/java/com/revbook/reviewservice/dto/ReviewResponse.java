package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Resenha;
import java.time.LocalDateTime;
import java.util.List;

/** Contrato público consumido pelo front-end — os nomes seguem o que o React já espera. */
public record ReviewResponse(
        Long id,
        String bookTitle,
        String author,
        String genre,
        String content,
        Long userId,
        String userName,
        LocalDateTime createdAt,
        List<RatingResponse> ratings,
        List<CommentResponse> comments) {

    public static ReviewResponse de(Resenha resenha) {
        return new ReviewResponse(
                resenha.getId(),
                resenha.getTitulo(),
                resenha.getAutor(),
                resenha.getGenero(),
                resenha.getConteudo(),
                resenha.getUsuarioId(),
                resenha.getNomeUsuario(),
                resenha.getCriadoEm(),
                resenha.getAvaliacoes().stream().map(RatingResponse::de).toList(),
                resenha.getComentarios().stream().map(CommentResponse::de).toList());
    }
}
