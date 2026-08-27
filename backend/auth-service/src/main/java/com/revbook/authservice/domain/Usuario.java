package com.revbook.authservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nome;

    private String googleId;

    private String avatar;

    protected Usuario() {
    }

    public Usuario(String email, String nome, String googleId, String avatar) {
        this.email = email;
        this.nome = nome;
        this.googleId = googleId;
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getAvatar() {
        return avatar;
    }
}
