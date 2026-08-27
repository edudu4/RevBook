package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.CreateReviewRequest;
import com.revbook.reviewservice.dto.RateReviewRequest;
import com.revbook.reviewservice.dto.ReviewResponse;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.LivroService;
import com.revbook.reviewservice.service.ResenhaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fala diretamente o contrato público em inglês — o Gateway (Spring Cloud Gateway) só
 * roteia e valida o JWT, sem transformar payload nenhum.
 */
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
                dados.googleBooksId(), dados.bookTitle(), dados.author(), dados.genre(), dados.coverUrl(),
                dados.content(), usuario.id(), usuario.nome());
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
}
