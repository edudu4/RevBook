package com.revbook.reviewservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Cache local dos livros escolhidos via Google Books API — populado sob demanda na
 * primeira vez que alguém seleciona aquele livro ao criar uma resenha (ver LivroService).
 */
@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleBooksId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    private String genero;

    private String capaUrl;

    protected Livro() {
    }

    public Livro(String googleBooksId, String titulo, String autor, String genero, String capaUrl) {
        this.googleBooksId = googleBooksId;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.capaUrl = capaUrl;
    }

    public Long getId() {
        return id;
    }

    public String getGoogleBooksId() {
        return googleBooksId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getGenero() {
        return genero;
    }

    public String getCapaUrl() {
        return capaUrl;
    }
}
