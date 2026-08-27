package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private ResenhaRepository resenhaRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private ComentarioService comentarioService;

    private Resenha resenhaComId(long id) {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null);
        Resenha resenha = new Resenha(livro, "Conteúdo", 1L, "Fulano", null);
        ReflectionTestUtils.setField(resenha, "id", id);
        return resenha;
    }

    private Comentario comentarioComId(long id, Long donoId, Resenha resenha) {
        Comentario comentario = new Comentario("Comentário original", donoId, "Autor", null, resenha);
        ReflectionTestUtils.setField(comentario, "id", id);
        return comentario;
    }

    @Test
    void criar_deveLancarExcecao_quandoResenhaNaoExiste() {
        when(resenhaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> comentarioService.criar(1L, "Ótimo!", 2L, "Ciclano", null))
                .isInstanceOf(NoSuchElementException.class);

        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void criar_deveSalvarComentario_quandoResenhaExiste() {
        Resenha resenha = resenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(inv -> inv.getArgument(0));

        Comentario resultado = comentarioService.criar(1L, "Ótimo!", 2L, "Ciclano", "https://avatar");

        assertThat(resultado.getConteudo()).isEqualTo("Ótimo!");
        assertThat(resultado.getResenha()).isEqualTo(resenha);
        assertThat(resultado.getAvatarUsuario()).isEqualTo("https://avatar");
    }

    @Test
    void excluir_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Resenha resenha = resenhaComId(1L);
        Comentario comentario = comentarioComId(10L, 2L, resenha);
        when(comentarioRepository.findById(10L)).thenReturn(Optional.of(comentario));

        assertThatThrownBy(() -> comentarioService.excluir(10L, 999L))
                .isInstanceOf(NaoAutorizadoException.class);

        verify(comentarioRepository, never()).delete(any());
    }

    @Test
    void excluir_deveRemover_quandoUsuarioEhDono() {
        Resenha resenha = resenhaComId(1L);
        Comentario comentario = comentarioComId(10L, 2L, resenha);
        when(comentarioRepository.findById(10L)).thenReturn(Optional.of(comentario));

        comentarioService.excluir(10L, 2L);

        verify(comentarioRepository).delete(comentario);
    }

    @Test
    void atualizar_deveAtualizarConteudo_quandoUsuarioEhDono() {
        Resenha resenha = resenhaComId(1L);
        Comentario comentario = comentarioComId(10L, 2L, resenha);
        when(comentarioRepository.findById(10L)).thenReturn(Optional.of(comentario));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(inv -> inv.getArgument(0));

        Comentario resultado = comentarioService.atualizar(10L, 2L, "Conteúdo editado");

        assertThat(resultado.getConteudo()).isEqualTo("Conteúdo editado");
        assertThat(resultado.getAtualizadoEm()).isNotNull();
    }

    @Test
    void atualizar_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Resenha resenha = resenhaComId(1L);
        Comentario comentario = comentarioComId(10L, 2L, resenha);
        when(comentarioRepository.findById(10L)).thenReturn(Optional.of(comentario));

        assertThatThrownBy(() -> comentarioService.atualizar(10L, 999L, "Tentativa indevida"))
                .isInstanceOf(NaoAutorizadoException.class);
    }
}
