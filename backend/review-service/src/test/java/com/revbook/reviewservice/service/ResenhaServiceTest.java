package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Avaliacao;
import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.domain.TipoNotificacao;
import com.revbook.reviewservice.exception.NaoAutorizadoException;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ResenhaServiceTest {

    @Mock
    private ResenhaRepository resenhaRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private ResenhaService resenhaService;

    private Resenha novaResenhaComId(long id) {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null);
        Resenha resenha = new Resenha(livro, "Excelente livro.", 1L, "Fulano", null);
        ReflectionTestUtils.setField(resenha, "id", id);
        return resenha;
    }

    @Test
    void criar_deveReaproveitarOuCriarLivroEEntaoSalvarResenha() {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null);
        when(livroService.buscarOuCriar("gb-1")).thenReturn(livro);
        when(resenhaRepository.save(any(Resenha.class))).thenAnswer(inv -> inv.getArgument(0));

        Resenha resultado = resenhaService.criar("gb-1", "Excelente livro.", 1L, "Fulano", "https://avatar");

        assertThat(resultado.getLivro()).isEqualTo(livro);
        assertThat(resultado.getConteudo()).isEqualTo("Excelente livro.");
        assertThat(resultado.getAvatarUsuario()).isEqualTo("https://avatar");
        verify(resenhaRepository).save(any(Resenha.class));
    }

    @Test
    void buscar_deveRetornarConteudoDaPagina() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(resenha)));

        List<Resenha> resultado = resenhaService.buscar("casmurro", "Romance", 0, 10);

        assertThat(resultado).containsExactly(resenha);
    }

    @Test
    void buscarPorId_deveRetornarResenha_quandoExiste() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));

        Resenha resultado = resenhaService.buscarPorId(1L);

        assertThat(resultado).isSameAs(resenha);
    }

    @Test
    void buscarPorId_deveLancarExcecao_quandoResenhaNaoExiste() {
        when(resenhaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenhaService.buscarPorId(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void avaliar_deveCriarNovaAvaliacao_quandoUsuarioAindaNaoAvaliou() {
        Resenha resenha = novaResenhaComId(1L);
        when(avaliacaoRepository.findByResenha_IdAndUsuarioId(1L, 2L)).thenReturn(Optional.empty());
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao resultado = resenhaService.avaliar(1L, 2L, 4, "Ciclano", "https://avatar");

        assertThat(resultado.getValor()).isEqualTo(4);
        assertThat(resultado.getUsuarioId()).isEqualTo(2L);
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(notificacaoService).notificar(TipoNotificacao.AVALIACAO, resenha, 2L, "Ciclano", "https://avatar");
    }

    @Test
    void avaliar_deveAtualizarAvaliacaoExistente_emVezDeCriarNova() {
        Resenha resenha = novaResenhaComId(1L);
        Avaliacao avaliacaoExistente = new Avaliacao(2L, 3, resenha);
        when(avaliacaoRepository.findByResenha_IdAndUsuarioId(1L, 2L)).thenReturn(Optional.of(avaliacaoExistente));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao resultado = resenhaService.avaliar(1L, 2L, 5, "Ciclano", "https://avatar");

        assertThat(resultado.getValor()).isEqualTo(5);
        assertThat(resultado).isSameAs(avaliacaoExistente);
        verify(resenhaRepository, never()).findById(any());
        verify(notificacaoService, never()).notificar(any(), any(), any(), any(), any());
    }

    @Test
    void avaliar_deveLancarExcecao_quandoValorForaDoIntervalo() {
        assertThatThrownBy(() -> resenhaService.avaliar(1L, 2L, 0, "Ciclano", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> resenhaService.avaliar(1L, 2L, 6, "Ciclano", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> resenhaService.avaliar(1L, 2L, null, "Ciclano", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void atualizar_deveAtualizarConteudo_quandoUsuarioEhDono() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));
        when(resenhaRepository.save(any(Resenha.class))).thenAnswer(inv -> inv.getArgument(0));

        Resenha resultado = resenhaService.atualizar(1L, 1L, "Conteúdo revisado");

        assertThat(resultado.getConteudo()).isEqualTo("Conteúdo revisado");
        assertThat(resultado.getAtualizadoEm()).isNotNull();
    }

    @Test
    void atualizar_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));

        assertThatThrownBy(() -> resenhaService.atualizar(1L, 999L, "Tentativa indevida"))
                .isInstanceOf(NaoAutorizadoException.class);

        verify(resenhaRepository, never()).save(any());
    }

    @Test
    void excluir_deveRemover_quandoUsuarioEhDono() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));

        resenhaService.excluir(1L, 1L);

        verify(resenhaRepository).delete(resenha);
    }

    @Test
    void excluir_deveLancarNaoAutorizado_quandoUsuarioNaoEhDono() {
        Resenha resenha = novaResenhaComId(1L);
        when(resenhaRepository.findById(1L)).thenReturn(Optional.of(resenha));

        assertThatThrownBy(() -> resenhaService.excluir(1L, 999L))
                .isInstanceOf(NaoAutorizadoException.class);

        verify(resenhaRepository, never()).delete(any(Resenha.class));
    }
}
