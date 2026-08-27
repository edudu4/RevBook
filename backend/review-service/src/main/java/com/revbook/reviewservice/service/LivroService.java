package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.googlebooks.GoogleBooksClient;
import com.revbook.reviewservice.googlebooks.LivroEncontrado;
import com.revbook.reviewservice.repository.LivroRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LivroService {

    private final LivroRepository livroRepository;
    private final GoogleBooksClient googleBooksClient;

    public LivroService(LivroRepository livroRepository, GoogleBooksClient googleBooksClient) {
        this.livroRepository = livroRepository;
        this.googleBooksClient = googleBooksClient;
    }

    @Transactional(readOnly = true)
    public List<LivroEncontrado> buscarNaGoogleBooks(String termo) {
        return googleBooksClient.buscar(termo);
    }

    /**
     * Primeira vez que esse googleBooksId aparece: grava um Livro novo (cache local).
     * Nas próximas vezes, reaproveita a linha já existente.
     */
    public Livro buscarOuCriar(String googleBooksId, String titulo, String autor, String genero, String capaUrl) {
        return livroRepository.findByGoogleBooksId(googleBooksId)
                .orElseGet(() -> livroRepository.save(new Livro(googleBooksId, titulo, autor, genero, capaUrl)));
    }

    @Transactional(readOnly = true)
    public List<String> listarGeneros() {
        return livroRepository.listarGenerosDistintos().stream().sorted().toList();
    }
}
