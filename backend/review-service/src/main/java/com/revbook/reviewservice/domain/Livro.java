package com.revbook.reviewservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String googleBooksId;

    @Column(nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String autor;

    @Column(columnDefinition = "text")
    private String genero;

    @Column(columnDefinition = "text")
    private String capaUrl;

    @Column(columnDefinition = "text")
    private String sinopse;

    protected Livro() {
    }

    public Livro(String googleBooksId, String titulo, String autor, String genero, String capaUrl, String sinopse) {
        this.googleBooksId = googleBooksId;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.capaUrl = capaUrl;
        this.sinopse = sinopse;
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

    public String getSinopse() {
        return sinopse;
    }
}
