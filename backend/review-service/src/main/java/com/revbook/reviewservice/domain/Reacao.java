package com.revbook.reviewservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"comentario_id", "usuarioId", "emoji"}))
public class Reacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 👍, ❤️, 😂, 😮, 😢, 🔥, 🎉, 💯 */
    @Column(nullable = false)
    private String emoji;

    @Column(nullable = false)
    private Long usuarioId;

    @ManyToOne
    @JoinColumn(name = "comentario_id", nullable = false)
    private Comentario comentario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    protected Reacao() {
    }

    public Reacao(String emoji, Long usuarioId, Comentario comentario) {
        this.emoji = emoji;
        this.usuarioId = usuarioId;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public String getEmoji() {
        return emoji;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
