package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.exception.LivroInvalidoException;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.googlebooks.GoogleBooksClient;
import com.revbook.reviewservice.googlebooks.LivroEncontrado;
import com.revbook.reviewservice.repository.LivroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LivroService {

    private final LivroRepository livroRepository;
    private final GoogleBooksClient googleBooksClient;

    @Value("${revbook.moderador.email:}")
    private String emailModerador;

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
                livroReal.genero(), livroReal.capaUrl(), livroReal.sinopse()));
    }

    @Transactional(readOnly = true)
    public List<String> listarGeneros() {
        return livroRepository.listarGenerosDistintos().stream().sorted().toList();
    }

    public int atualizarSinopsesFaltantes(String emailSolicitante) {
        if (emailModerador == null || emailModerador.isBlank() || !emailModerador.equalsIgnoreCase(emailSolicitante)) {
            throw new NaoAutorizadoException("Apenas o moderador pode executar essa ação");
        }

        List<Livro> semSinopse = livroRepository.findBySinopseIsNull();
        int atualizados = 0;
        for (Livro livro : semSinopse) {
            LivroEncontrado encontrado = googleBooksClient.buscarPorId(livro.getGoogleBooksId()).orElse(null);
            if (encontrado != null && encontrado.sinopse() != null && !encontrado.sinopse().isBlank()) {
                livro.setSinopse(encontrado.sinopse());
                livroRepository.save(livro);
                atualizados++;
            }
        }
        return atualizados;
    }
}
