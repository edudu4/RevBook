package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Reacao;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ReacaoRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReacaoServiceTest {

    @Mock
    private ReacaoRepository reacaoRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ReacaoService reacaoService;

    private Comentario comentarioComId(long id) {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null, null);
        Resenha resenha = new Resenha(livro, "Conteúdo", 1L, "Fulano", null);
        Comentario comentario = new Comentario("Comentário", 1L, "Fulano", null, resenha);
        ReflectionTestUtils.setField(comentario, "id", id);
        return comentario;
    }

    @Test
    void alternar_deveCriarReacao_quandoAindaNaoExiste() {
        Comentario comentario = comentarioComId(5L);
        when(reacaoRepository.findByComentario_IdAndUsuarioIdAndEmoji(5L, 2L, "👍")).thenReturn(Optional.empty());
        when(comentarioRepository.findById(5L)).thenReturn(Optional.of(comentario));
        when(reacaoRepository.save(any(Reacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Reacao> resultado = reacaoService.alternar(5L, 2L, "👍");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmoji()).isEqualTo("👍");
        verify(reacaoRepository, never()).delete(any());
    }

    @Test
    void alternar_deveRemoverReacao_quandoJaExiste() {
        Comentario comentario = comentarioComId(5L);
        Reacao reacaoExistente = new Reacao("👍", 2L, comentario);
        when(reacaoRepository.findByComentario_IdAndUsuarioIdAndEmoji(5L, 2L, "👍"))
                .thenReturn(Optional.of(reacaoExistente));

        Optional<Reacao> resultado = reacaoService.alternar(5L, 2L, "👍");

        assertThat(resultado).isEmpty();
        verify(reacaoRepository).delete(reacaoExistente);
        verify(reacaoRepository, never()).save(any());
    }

    @Test
    void alternar_deveLancarExcecao_quandoComentarioNaoExiste() {
        when(reacaoRepository.findByComentario_IdAndUsuarioIdAndEmoji(999L, 2L, "👍")).thenReturn(Optional.empty());
        when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reacaoService.alternar(999L, 2L, "👍"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void remover_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Comentario comentario = comentarioComId(5L);
        Reacao reacao = new Reacao("🔥", 2L, comentario);
        ReflectionTestUtils.setField(reacao, "id", 20L);
        when(reacaoRepository.findById(20L)).thenReturn(Optional.of(reacao));

        assertThatThrownBy(() -> reacaoService.remover(20L, 999L))
                .isInstanceOf(NaoAutorizadoException.class);

        verify(reacaoRepository, never()).delete(any());
    }

    @Test
    void remover_deveRemover_quandoUsuarioEhDono() {
        Comentario comentario = comentarioComId(5L);
        Reacao reacao = new Reacao("🔥", 2L, comentario);
        ReflectionTestUtils.setField(reacao, "id", 20L);
        when(reacaoRepository.findById(20L)).thenReturn(Optional.of(reacao));

        reacaoService.remover(20L, 2L);

        verify(reacaoRepository).delete(reacao);
    }
}
