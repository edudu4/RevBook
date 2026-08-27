package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Notificacao;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.domain.TipoNotificacao;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.NotificacaoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificacaoService {

    private static final int LIMITE_LISTAGEM = 30;

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public void notificar(TipoNotificacao tipo, Resenha resenha, Long autorId, String nomeAtor, String avatarAtor) {
        if (resenha.getUsuarioId().equals(autorId)) {
            return;
        }

        Notificacao notificacao = new Notificacao(resenha.getUsuarioId(), tipo, resenha, nomeAtor, avatarAtor);
        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarPorUsuario(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId, PageRequest.of(0, LIMITE_LISTAGEM));
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    public void marcarComoLida(Long notificacaoId, Long usuarioId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new NoSuchElementException("Notificação não encontrada"));

        if (!notificacao.getUsuarioId().equals(usuarioId)) {
            throw new NaoAutorizadoException("Usuário não autorizado a alterar esta notificação");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    public void marcarTodasComoLidas(Long usuarioId) {
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId);
        naoLidas.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(naoLidas);
    }
}
