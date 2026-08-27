package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.exception.LivroInvalidoException;
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

    public Livro buscarOuCriar(String googleBooksId) {
        return livroRepository.findByGoogleBooksId(googleBooksId)
                .orElseGet(() -> criarComDadosVerificados(googleBooksId));
    }

    private Livro criarComDadosVerificados(String googleBooksId) {
        LivroEncontrado livroReal = googleBooksClient.buscarPorId(googleBooksId)
                .orElseThrow(() -> new LivroInvalidoException(
                        "Livro não encontrado na Google Books API — não é possível criar uma resenha para ele"));

        return livroRepository.save(new Livro(
                livroReal.googleBooksId(), livroReal.titulo(), livroReal.autor(),
                livroReal.genero(), livroReal.capaUrl()));
    }

    @Transactional(readOnly = true)
    public List<String> listarGeneros() {
        return livroRepository.listarGenerosDistintos().stream().sorted().toList();
    }
}
