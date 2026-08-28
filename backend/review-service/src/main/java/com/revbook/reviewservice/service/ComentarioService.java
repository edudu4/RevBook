package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.domain.TipoNotificacao;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ResenhaRepository resenhaRepository;
    private final NotificacaoService notificacaoService;

    @Value("${revbook.moderador.email:}")
    private String emailModerador;

    public ComentarioService(
            ComentarioRepository comentarioRepository,
            ResenhaRepository resenhaRepository,
            NotificacaoService notificacaoService) {
        this.comentarioRepository = comentarioRepository;
        this.resenhaRepository = resenhaRepository;
        this.notificacaoService = notificacaoService;
    }

    public Comentario criar(Long resenhaId, String conteudo, Long usuarioId, String nomeUsuario, String avatarUsuario) {
        Resenha resenha = resenhaRepository.findById(resenhaId)
                .orElseThrow(() -> new NoSuchElementException("Resenha não encontrada"));

        Comentario comentario = new Comentario(conteudo, usuarioId, nomeUsuario, avatarUsuario, resenha);
        comentario = comentarioRepository.save(comentario);

        notificacaoService.notificar(TipoNotificacao.COMENTARIO, resenha, usuarioId, nomeUsuario, avatarUsuario);

        return comentario;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorResenha(Long resenhaId) {
        return comentarioRepository.findByResenha_IdOrderByCriadoEmDesc(resenhaId);
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorUsuario(Long usuarioId) {
        return comentarioRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public void excluir(Long comentarioId, Long usuarioId, String emailSolicitante) {
        Comentario comentario = buscarDoUsuario(comentarioId, usuarioId, emailSolicitante);
        comentarioRepository.delete(comentario);
    }

    public Comentario atualizar(Long comentarioId, Long usuarioId, String emailSolicitante, String conteudo) {
        Comentario comentario = buscarDoUsuario(comentarioId, usuarioId, emailSolicitante);
        comentario.setConteudo(conteudo);
        comentario.setAtualizadoEm(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    private boolean ehModerador(String email) {
        return email != null && emailModerador != null && !emailModerador.isBlank()
                && emailModerador.equalsIgnoreCase(email);
    }

    private Comentario buscarDoUsuario(Long comentarioId, Long usuarioId, String emailSolicitante) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new NoSuchElementException("Comentário não encontrado"));

        if (!comentario.getUsuarioId().equals(usuarioId) && !ehModerador(emailSolicitante)) {
            throw new NaoAutorizadoException("Usuário não autorizado a alterar este comentário");
        }

        return comentario;
    }
}
