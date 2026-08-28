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
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String nomeUsuario;

    @Column(columnDefinition = "text")
    private String avatarUsuario;

    @ManyToOne
    @JoinColumn(name = "resenha_id", nullable = false)
    private Resenha resenha;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "comentario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reacao> reacoes = new ArrayList<>();

    protected Comentario() {
    }

    public Comentario(String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario, Resenha resenha) {
        this.conteudo = conteudo;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.avatarUsuario = avatarUsuario;
        this.resenha = resenha;
    }

    public Long getId() {
        return id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
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

    public Resenha getResenha() {
        return resenha;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<Reacao> getReacoes() {
        return reacoes;
    }
}
