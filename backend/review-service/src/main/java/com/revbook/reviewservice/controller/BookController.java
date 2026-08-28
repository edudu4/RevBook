package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.BookSearchResult;
import com.revbook.reviewservice.security.UsuarioAutenticado;
import com.revbook.reviewservice.security.UsuarioLogado;
import com.revbook.reviewservice.service.LivroService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final LivroService livroService;

    public BookController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping("/books/search")
    public List<BookSearchResult> buscar(@RequestParam String q) {
        return livroService.buscarNaGoogleBooks(q).stream().map(BookSearchResult::de).toList();
    }

    @PostMapping("/books/backfill-sinopse")
    public Map<String, Integer> atualizarSinopsesFaltantes(@UsuarioLogado UsuarioAutenticado usuario) {
        int atualizados = livroService.atualizarSinopsesFaltantes(usuario.email());
        return Map.of("atualizados", atualizados);
    }
}
