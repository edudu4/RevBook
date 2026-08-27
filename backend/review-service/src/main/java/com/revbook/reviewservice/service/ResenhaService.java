package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Avaliacao;
import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import com.revbook.reviewservice.repository.ResenhaSpecifications;
import java.time.LocalDateTime;
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

    public Resenha criar(String googleBooksId, String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario) {
        Livro livro = livroService.buscarOuCriar(googleBooksId);
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

    public Resenha atualizar(Long resenhaId, Long usuarioId, String conteudo) {
        Resenha resenha = buscarDoUsuario(resenhaId, usuarioId);
        resenha.setConteudo(conteudo);
        resenha.setAtualizadoEm(LocalDateTime.now());
        return resenhaRepository.save(resenha);
    }

    public void excluir(Long resenhaId, Long usuarioId) {
        Resenha resenha = buscarDoUsuario(resenhaId, usuarioId);
        resenhaRepository.delete(resenha);
    }

    private Resenha buscarDoUsuario(Long resenhaId, Long usuarioId) {
        Resenha resenha = buscarPorId(resenhaId);

        if (!resenha.getUsuarioId().equals(usuarioId)) {
            throw new NaoAutorizadoException("Usuário não autorizado a alterar esta resenha");
        }

        return resenha;
    }
}
