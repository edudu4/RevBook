package com.revbook.reviewservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;

    @ManyToOne
    @JoinColumn(name = "resenha_id", nullable = false)
    private Resenha resenha;

    @Column(nullable = false)
    private String nomeAtor;

    private String avatarAtor;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    protected Notificacao() {
    }

    public Notificacao(Long usuarioId, TipoNotificacao tipo, Resenha resenha, String nomeAtor, String avatarAtor) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.resenha = resenha;
        this.nomeAtor = nomeAtor;
        this.avatarAtor = avatarAtor;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public Resenha getResenha() {
        return resenha;
    }

    public String getNomeAtor() {
        return nomeAtor;
    }

    public String getAvatarAtor() {
        return avatarAtor;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
