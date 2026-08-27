package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Avaliacao;
import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import com.revbook.reviewservice.repository.ResenhaSpecifications;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResenhaService {

    private final ResenhaRepository resenhaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final LivroService livroService;

    public ResenhaService(
            ResenhaRepository resenhaRepository, AvaliacaoRepository avaliacaoRepository, LivroService livroService) {
        this.resenhaRepository = resenhaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.livroService = livroService;
    }

    public Resenha criar(
            String googleBooksId, String titulo, String autor, String genero, String capaUrl,
            String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario) {
        Livro livro = livroService.buscarOuCriar(googleBooksId, titulo, autor, genero, capaUrl);
        Resenha resenha = new Resenha(livro, conteudo, usuarioId, nomeUsuario, avatarUsuario);
        return resenhaRepository.save(resenha);
    }

    @Transactional(readOnly = true)
    public List<Resenha> listarTodas() {
        return resenhaRepository.findAllByOrderByCriadoEmDesc();
    }

    @Transactional(readOnly = true)
    public Resenha buscarPorId(Long id) {
        return resenhaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resenha não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Resenha> listarPorUsuario(Long usuarioId) {
        return resenhaRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Resenha> buscar(String titulo, String autor, String genero) {
        return resenhaRepository.findAll(
                ResenhaSpecifications.comFiltros(titulo, autor, genero),
                Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    public Avaliacao avaliar(Long resenhaId, Long usuarioId, Integer valor) {
        if (valor == null || valor < 1 || valor > 5) {
            throw new IllegalArgumentException("A avaliação deve ser um valor entre 1 e 5 estrelas");
        }

        Avaliacao avaliacao = avaliacaoRepository.findByResenha_IdAndUsuarioId(resenhaId, usuarioId)
                .orElse(null);

        if (avaliacao != null) {
            avaliacao.setValor(valor);
        } else {
            Resenha resenha = buscarPorId(resenhaId);
            avaliacao = new Avaliacao(usuarioId, valor, resenha);
        }

        return avaliacaoRepository.save(avaliacao);
    }
}
