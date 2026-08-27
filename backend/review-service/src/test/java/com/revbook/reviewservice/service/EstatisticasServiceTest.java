package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EstatisticasServiceTest {

    @Mock
    private ResenhaRepository resenhaRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private EstatisticasService estatisticasService;

    @Test
    void calcular_deveSomarContagensDosTresRepositorios() {
        Livro livro = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null);
        Resenha resenhaA = new Resenha(livro, "Ótimo", 1L, "Fulano", null);
        Resenha resenhaB = new Resenha(livro, "Também ótimo", 1L, "Fulano", null);

        when(resenhaRepository.findByUsuarioIdOrderByCriadoEmDesc(1L)).thenReturn(List.of(resenhaA, resenhaB));
        when(comentarioRepository.countByUsuarioId(1L)).thenReturn(4L);
        when(avaliacaoRepository.countByResenha_UsuarioId(1L)).thenReturn(7L);

        var resultado = estatisticasService.calcular(1L);

        assertThat(resultado.reviewCount()).isEqualTo(2);
        assertThat(resultado.commentCount()).isEqualTo(4);
        assertThat(resultado.totalRatingsReceived()).isEqualTo(7);
    }
}
