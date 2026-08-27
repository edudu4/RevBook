package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Reacao;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ReacaoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReacaoService {

    private final ReacaoRepository reacaoRepository;
    private final ComentarioRepository comentarioRepository;

    public ReacaoService(ReacaoRepository reacaoRepository, ComentarioRepository comentarioRepository) {
        this.reacaoRepository = reacaoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    public Optional<Reacao> alternar(Long comentarioId, Long usuarioId, String emoji) {
        Optional<Reacao> existente =
                reacaoRepository.findByComentario_IdAndUsuarioIdAndEmoji(comentarioId, usuarioId, emoji);

        if (existente.isPresent()) {
            reacaoRepository.delete(existente.get());
            return Optional.empty();
        }

        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));

        Reacao reacao = new Reacao(emoji, usuarioId, comentario);
        return Optional.of(reacaoRepository.save(reacao));
    }

    @Transactional(readOnly = true)
    public List<Reacao> listarPorComentario(Long comentarioId) {
        return reacaoRepository.findByComentario_Id(comentarioId);
    }

    public void remover(Long reacaoId, Long usuarioId) {
        Reacao reacao = reacaoRepository.findById(reacaoId)
                .orElseThrow(() -> new NoSuchElementException("Reação não encontrada"));

        if (!reacao.getUsuarioId().equals(usuarioId)) {
            throw new NaoAutorizadoException("Usuário não autorizado a remover esta reação");
        }

        reacaoRepository.delete(reacao);
    }
}
