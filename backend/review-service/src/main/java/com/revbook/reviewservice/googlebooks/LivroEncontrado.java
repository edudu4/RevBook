package com.revbook.reviewservice.googlebooks;

public record LivroEncontrado(
        String googleBooksId, String titulo, String autor, String genero, String capaUrl, String sinopse) {
}
