package com.revbook.reviewservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Resenha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(columnDefinition = "text", nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String nomeUsuario;

    private String avatarUsuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "resenha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @OneToMany(mappedBy = "resenha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    protected Resenha() {
    }

    public Resenha(Livro livro, String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario) {
        this.livro = livro;
        this.conteudo = conteudo;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.avatarUsuario = avatarUsuario;
    }

    public Long getId() {
        return id;
    }

    public Livro getLivro() {
        return livro;
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

    public String getAvatarUsuario() {
        return avatarUsuario;
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
