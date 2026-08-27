package com.revbook.reviewservice.googlebooks;

/** Resultado normalizado de uma busca na Google Books API. */
public record LivroEncontrado(String googleBooksId, String titulo, String autor, String genero, String capaUrl) {
}
