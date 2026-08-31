package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.CommentContentRequest;
import com.revbook.reviewservice.dto.CommentResponse;
import com.revbook.reviewservice.dto.CommentWithReviewResponse;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.ComentarioService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @PostMapping("/reviews/{reviewId}/comments")
    public CommentResponse criar(
            @PathVariable Long reviewId,
            @RequestBody CommentContentRequest dados,
            @UsuarioLogado UsuarioAutenticado usuario) {
        var comentario = comentarioService.criar(
                reviewId, dados.content(), usuario.id(), usuario.nome(), usuario.avatar(), dados.parentId());
        return CommentResponse.de(comentario);
    }

    @GetMapping("/reviews/{reviewId}/comments")
    public List<CommentResponse> listarPorResenha(@PathVariable Long reviewId) {
        return comentarioService.listarPorResenha(reviewId).stream().map(CommentResponse::de).toList();
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentWithReviewResponse> listarPorUsuario(@PathVariable Long userId) {
        return comentarioService.listarPorUsuario(userId).stream().map(CommentWithReviewResponse::de).toList();
    }

    @DeleteMapping("/comments/{commentId}")
    public void excluir(@PathVariable Long commentId, @UsuarioLogado UsuarioAutenticado usuario) {
        comentarioService.excluir(commentId, usuario.id(), usuario.email());
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse atualizar(
            @PathVariable Long commentId,
            @RequestBody CommentContentRequest dados,
            @UsuarioLogado UsuarioAutenticado usuario) {
        return CommentResponse.de(
                comentarioService.atualizar(commentId, usuario.id(), usuario.email(), dados.content()));
    }
}
