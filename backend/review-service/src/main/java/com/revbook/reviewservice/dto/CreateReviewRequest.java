package com.revbook.reviewservice.dto;

public record CreateReviewRequest(
        String googleBooksId,
        String bookTitle,
        String author,
        String genre,
        String coverUrl,
        String content) {
}
