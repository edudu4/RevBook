package com.revbook.reviewservice.dto;

/**
 * O livro vem escolhido de um resultado de busca (GET /books/search) — sem digitação livre.
 * bookTitle/author/genre/coverUrl não são mais lidos no servidor: o backend não confia em
 * dados de livro vindos do cliente, sempre revalida googleBooksId contra a Google Books API
 * (ver LivroService) — os campos ficam aqui só por compatibilidade com o que o front já envia.
 */
public record CreateReviewRequest(
        String googleBooksId,
        String bookTitle,
        String author,
        String genre,
        String coverUrl,
        String content) {
}
