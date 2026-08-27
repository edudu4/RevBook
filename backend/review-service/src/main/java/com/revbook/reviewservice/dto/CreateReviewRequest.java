package com.revbook.reviewservice.dto;

/** O livro vem escolhido de um resultado de busca (GET /books/search) — sem digitação livre. */
public record CreateReviewRequest(
        String googleBooksId,
        String bookTitle,
        String author,
        String genre,
        String coverUrl,
        String content) {
}
