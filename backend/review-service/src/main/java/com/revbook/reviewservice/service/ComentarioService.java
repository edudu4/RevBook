package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ResenhaRepository resenhaRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, ResenhaRepository resenhaRepository) {
        this.comentarioRepository = comentarioRepository;
        this.resenhaRepository = resenhaRepository;
    }

    public Comentario criar(Long resenhaId, String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario) {
        Resenha resenha = resenhaRepository.findById(resenhaId)
                .orElseThrow(() -> new NoSuchElementException("Resenha não encontrada"));

        Comentario comentario = new Comentario(conteudo, usuarioId, nomeUsuario, avatarUsuario, resenha);
        return comentarioRepository.save(comentario);
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorResenha(Long resenhaId) {
        return comentarioRepository.findByResenha_IdOrderByCriadoEmDesc(resenhaId);
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorUsuario(Long usuarioId) {
        return comentarioRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public void excluir(Long comentarioId, Long usuarioId) {
        Comentario comentario = buscarDoUsuario(comentarioId, usuarioId);
        comentarioRepository.delete(comentario);
    }

    public Comentario atualizar(Long comentarioId, Long usuarioId, String conteudo) {
        Comentario comentario = buscarDoUsuario(comentarioId, usuarioId);
        comentario.setConteudo(conteudo);
        comentario.setAtualizadoEm(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    private Comentario buscarDoUsuario(Long comentarioId, Long usuarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));

        if (!comentario.getUsuarioId().equals(usuarioId)) {
            throw new NaoAutorizadoException("Usuário não autorizado a alterar este comentário");
        }

        return comentario;
    }
}
