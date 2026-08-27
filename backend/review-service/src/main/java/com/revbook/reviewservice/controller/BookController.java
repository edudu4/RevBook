package com.revbook.reviewservice.controller;

import com.revbook.reviewservice.dto.BookSearchResult;
import com.revbook.reviewservice.service.LivroService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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
}
