package com.revbook.reviewservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer valor;

    @ManyToOne
    @JoinColumn(name = "resenha_id", nullable = false)
    private Resenha resenha;

    protected Avaliacao() {
    }

    public Avaliacao(Long usuarioId, Integer valor, Resenha resenha) {
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.resenha = resenha;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public Resenha getResenha() {
        return resenha;
    }
}
