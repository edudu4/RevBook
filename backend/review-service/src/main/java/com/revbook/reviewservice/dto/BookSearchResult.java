package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.googlebooks.LivroEncontrado;

public record BookSearchResult(
        String googleBooksId, String title, String author, String genre, String coverUrl, String synopsis) {

    public static BookSearchResult de(LivroEncontrado livro) {
        return new BookSearchResult(
                livro.googleBooksId(), livro.titulo(), livro.autor(), livro.genero(), livro.capaUrl(),
                livro.sinopse());
    }
}
