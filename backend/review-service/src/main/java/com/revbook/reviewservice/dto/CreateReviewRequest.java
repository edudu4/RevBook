package com.revbook.reviewservice.dto;

public record CreateReviewRequest(String bookTitle, String author, String genre, String content) {
}
