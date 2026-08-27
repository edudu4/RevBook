package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Notificacao;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.domain.TipoNotificacao;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.NotificacaoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private Resenha resenhaDoUsuario(long donoId) {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null);
        Resenha resenha = new Resenha(livro, "Conteúdo", donoId, "Dono", null);
        ReflectionTestUtils.setField(resenha, "id", 1L);
        return resenha;
    }

    private Notificacao notificacaoComId(long id, long usuarioId) {
        Notificacao notificacao =
                new Notificacao(usuarioId, TipoNotificacao.COMENTARIO, resenhaDoUsuario(usuarioId), "Ciclano", null);
        ReflectionTestUtils.setField(notificacao, "id", id);
        return notificacao;
    }

    @Test
    void notificar_deveSalvar_quandoAtorNaoEhDonoDaResenha() {
        Resenha resenha = resenhaDoUsuario(1L);

        notificacaoService.notificar(TipoNotificacao.COMENTARIO, resenha, 2L, "Ciclano", "https://avatar");

        verify(notificacaoRepository).save(any(Notificacao.class));
    }

    @Test
    void notificar_naoDeveSalvar_quandoAtorEhDonoDaResenha() {
        Resenha resenha = resenhaDoUsuario(1L);

        notificacaoService.notificar(TipoNotificacao.COMENTARIO, resenha, 1L, "Dono", null);

        verify(notificacaoRepository, never()).save(any());
    }

    @Test
    void marcarComoLida_deveMarcar_quandoUsuarioEhDono() {
        Notificacao notificacao = notificacaoComId(10L, 1L);
        when(notificacaoRepository.findById(10L)).thenReturn(Optional.of(notificacao));

        notificacaoService.marcarComoLida(10L, 1L);

        assertThat(notificacao.isLida()).isTrue();
        verify(notificacaoRepository).save(notificacao);
    }

    @Test
    void marcarComoLida_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Notificacao notificacao = notificacaoComId(10L, 1L);
        when(notificacaoRepository.findById(10L)).thenReturn(Optional.of(notificacao));

        assertThatThrownBy(() -> notificacaoService.marcarComoLida(10L, 999L))
                .isInstanceOf(NaoAutorizadoException.class);
    }

    @Test
    void marcarComoLida_deveLancarExcecao_quandoNotificacaoNaoExiste() {
        when(notificacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacaoService.marcarComoLida(99L, 1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void marcarTodasComoLidas_deveMarcarTodasAsNaoLidas() {
        Notificacao naoLida1 = notificacaoComId(1L, 1L);
        Notificacao naoLida2 = notificacaoComId(2L, 1L);
        when(notificacaoRepository.findByUsuarioIdAndLidaFalse(1L)).thenReturn(List.of(naoLida1, naoLida2));

        notificacaoService.marcarTodasComoLidas(1L);

        assertThat(naoLida1.isLida()).isTrue();
        assertThat(naoLida2.isLida()).isTrue();
        verify(notificacaoRepository).saveAll(List.of(naoLida1, naoLida2));
    }
}
