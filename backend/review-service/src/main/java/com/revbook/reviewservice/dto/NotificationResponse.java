package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Notificacao;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        Long reviewId,
        String bookTitle,
        String actorName,
        String actorAvatar,
        boolean read,
        LocalDateTime createdAt) {

    public static NotificationResponse de(Notificacao notificacao) {
        return new NotificationResponse(
                notificacao.getId(),
                notificacao.getTipo().name(),
                notificacao.getResenha().getId(),
                notificacao.getResenha().getLivro().getTitulo(),
                notificacao.getNomeAtor(),
                notificacao.getAvatarAtor(),
                notificacao.isLida(),
                notificacao.getCriadoEm());
    }
}
