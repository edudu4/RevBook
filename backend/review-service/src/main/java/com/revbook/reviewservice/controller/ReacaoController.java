package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.AddReactionRequest;
import com.revbook.reviewservice.dto.ReactionResponse;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.ReacaoService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReacaoController {

    private final ReacaoService reacaoService;

    public ReacaoController(ReacaoService reacaoService) {
        this.reacaoService = reacaoService;
    }

    @PostMapping("/comments/{commentId}/reactions")
    public void alternar(
            @PathVariable Long commentId,
            @RequestBody AddReactionRequest dados,
            @UsuarioLogado UsuarioAutenticado usuario) {
        reacaoService.alternar(commentId, usuario.id(), dados.emoji());
    }

    @GetMapping("/comments/{commentId}/reactions")
    public List<ReactionResponse> listarPorComentario(@PathVariable Long commentId) {
        return ReactionResponse.agrupar(reacaoService.listarPorComentario(commentId));
    }

    @DeleteMapping("/reactions/{reactionId}")
    public void remover(@PathVariable Long reactionId, @UsuarioLogado UsuarioAutenticado usuario) {
        reacaoService.remover(reactionId, usuario.id());
    }
}
