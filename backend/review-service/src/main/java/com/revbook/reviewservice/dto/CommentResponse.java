package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Comentario;
import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        Long userId,
        String userName,
        String userAvatar,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ReactionResponse> reactions,
        Long parentId) {

    public static CommentResponse de(Comentario comentario) {
        return new CommentResponse(
                comentario.getId(),
                comentario.getConteudo(),
                comentario.getUsuarioId(),
                comentario.getNomeUsuario(),
                comentario.getAvatarUsuario(),
                comentario.getCriadoEm(),
                comentario.getAtualizadoEm(),
                ReactionResponse.agrupar(comentario.getReacoes()),
                comentario.getComentarioPai() != null ? comentario.getComentarioPai().getId() : null);
    }
}
