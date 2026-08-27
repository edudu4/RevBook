package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Comentario;
import java.time.LocalDateTime;
import java.util.List;

public record CommentWithReviewResponse(
        Long id,
        String content,
        Long userId,
        String userName,
        String userAvatar,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ReactionResponse> reactions,
        ReviewSummaryResponse review) {

    public static CommentWithReviewResponse de(Comentario comentario) {
        return new CommentWithReviewResponse(
                comentario.getId(),
                comentario.getConteudo(),
                comentario.getUsuarioId(),
                comentario.getNomeUsuario(),
                comentario.getAvatarUsuario(),
                comentario.getCriadoEm(),
                comentario.getAtualizadoEm(),
                ReactionResponse.agrupar(comentario.getReacoes()),
                new ReviewSummaryResponse(
                        comentario.getResenha().getId(), comentario.getResenha().getLivro().getTitulo()));
    }

    public record ReviewSummaryResponse(Long id, String bookTitle) {
    }
}
