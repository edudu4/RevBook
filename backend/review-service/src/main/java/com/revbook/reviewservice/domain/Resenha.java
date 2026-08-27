package com.revbook.reviewservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Resenha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    private String genero;

    @Column(columnDefinition = "text", nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String nomeUsuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "resenha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @OneToMany(mappedBy = "resenha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    protected Resenha() {
    }

    public Resenha(String titulo, String autor, String genero, String conteudo, Long usuarioId, String nomeUsuario) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.conteudo = conteudo;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
    }

    public Long getId() {
        return id;
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

    public String getConteudo() {
        return conteudo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }
}
