package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Avaliacao;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import com.revbook.reviewservice.repository.ResenhaSpecifications;
import java.util.Comparator;
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

    public ResenhaService(ResenhaRepository resenhaRepository, AvaliacaoRepository avaliacaoRepository) {
        this.resenhaRepository = resenhaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public Resenha criar(String titulo, String autor, String genero, String conteudo, Long usuarioId, String nomeUsuario) {
        Resenha resenha = new Resenha(titulo, autor, genero, conteudo, usuarioId, nomeUsuario);
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

    @Transactional(readOnly = true)
    public List<String> listarGeneros() {
        return resenhaRepository.findAll().stream()
                .map(Resenha::getGenero)
                .filter(genero -> genero != null && !genero.isBlank())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    public Avaliacao avaliar(Long resenhaId, Long usuarioId, Integer valor) {
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
