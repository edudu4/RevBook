package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.CreateReviewRequest;
import com.revbook.reviewservice.dto.RateReviewRequest;
import com.revbook.reviewservice.dto.ReviewResponse;
import com.revbook.reviewservice.dto.UpdateReviewRequest;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.LivroService;
import com.revbook.reviewservice.service.ResenhaService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResenhaController {

    private final ResenhaService resenhaService;
    private final LivroService livroService;

    public ResenhaController(ResenhaService resenhaService, LivroService livroService) {
        this.resenhaService = resenhaService;
        this.livroService = livroService;
    }

    @PostMapping("/reviews")
    public ReviewResponse criar(@RequestBody CreateReviewRequest dados, @UsuarioLogado UsuarioAutenticado usuario) {
        var resenha = resenhaService.criar(
                dados.googleBooksId(), dados.content(), usuario.id(), usuario.nome(), usuario.avatar());
        return ReviewResponse.de(resenha);
    }

    @GetMapping("/reviews")
    public List<ReviewResponse> listarTodas() {
        return resenhaService.listarTodas().stream().map(ReviewResponse::de).toList();
    }

    @GetMapping("/reviews/search")
    public List<ReviewResponse> buscar(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {
        return resenhaService.buscar(title, author, genre).stream().map(ReviewResponse::de).toList();
    }

    @GetMapping("/genres")
    public List<String> listarGeneros() {
        return livroService.listarGeneros();
    }

    @GetMapping("/reviews/{id}")
    public ReviewResponse buscarPorId(@PathVariable Long id) {
        return ReviewResponse.de(resenhaService.buscarPorId(id));
    }

    @GetMapping("/users/{userId}/reviews")
    public List<ReviewResponse> listarPorUsuario(@PathVariable Long userId) {
        return resenhaService.listarPorUsuario(userId).stream().map(ReviewResponse::de).toList();
    }

    @PostMapping("/reviews/rate")
    public void avaliar(@RequestBody RateReviewRequest dados, @UsuarioLogado UsuarioAutenticado usuario) {
        resenhaService.avaliar(dados.reviewId(), usuario.id(), dados.value());
    }

    @PutMapping("/reviews/{id}")
    public ReviewResponse atualizar(
            @PathVariable Long id,
            @RequestBody UpdateReviewRequest dados,
            @UsuarioLogado UsuarioAutenticado usuario) {
        return ReviewResponse.de(resenhaService.atualizar(id, usuario.id(), dados.content()));
    }

    @DeleteMapping("/reviews/{id}")
    public void excluir(@PathVariable Long id, @UsuarioLogado UsuarioAutenticado usuario) {
        resenhaService.excluir(id, usuario.id());
    }
}
