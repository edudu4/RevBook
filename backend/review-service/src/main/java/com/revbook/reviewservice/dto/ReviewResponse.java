package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Resenha;
import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        String bookTitle,
        String author,
        String genre,
        String coverUrl,
        String synopsis,
        String content,
        Long userId,
        String userName,
        String userAvatar,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<RatingResponse> ratings,
        List<CommentResponse> comments) {

    public static ReviewResponse de(Resenha resenha) {
        var livro = resenha.getLivro();
        return new ReviewResponse(
                resenha.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getGenero(),
                livro.getCapaUrl(),
                livro.getSinopse(),
                resenha.getConteudo(),
                resenha.getUsuarioId(),
                resenha.getNomeUsuario(),
                resenha.getAvatarUsuario(),
                resenha.getCriadoEm(),
                resenha.getAtualizadoEm(),
                resenha.getAvaliacoes().stream().map(RatingResponse::de).toList(),
                resenha.getComentarios().stream().map(CommentResponse::de).toList());
    }
}
