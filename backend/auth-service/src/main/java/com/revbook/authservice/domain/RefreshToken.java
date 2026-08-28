package com.revbook.authservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String token;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    protected RefreshToken() {
    }

    public RefreshToken(String token, Long usuarioId, LocalDateTime expiraEm) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.expiraEm = expiraEm;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public boolean expirado() {
        return LocalDateTime.now().isAfter(expiraEm);
    }
}
