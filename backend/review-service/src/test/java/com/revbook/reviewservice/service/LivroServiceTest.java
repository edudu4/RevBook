package com.revbook.reviewservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.exception.LivroInvalidoException;
import com.revbook.reviewservice.googlebooks.GoogleBooksClient;
import com.revbook.reviewservice.googlebooks.LivroEncontrado;
import com.revbook.reviewservice.repository.LivroRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private GoogleBooksClient googleBooksClient;

    @InjectMocks
    private LivroService livroService;

    @Test
    void buscarNaGoogleBooks_deveDelegarParaOClient() {
        var esperado = List.of(new LivroEncontrado("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null, null));
        when(googleBooksClient.buscar("dom casmurro")).thenReturn(esperado);

        var resultado = livroService.buscarNaGoogleBooks("dom casmurro");

        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void buscarOuCriar_deveReaproveitarLivroExistente_semSalvarDeNovo() {
        Livro existente = new Livro("gb-1", "Dom Casmurro", "Machado de Assis", "Romance", null, null);
        when(livroRepository.findByGoogleBooksId("gb-1")).thenReturn(Optional.of(existente));

        Livro resultado = livroService.buscarOuCriar("gb-1");

        assertThat(resultado).isSameAs(existente);
        verify(livroRepository, never()).save(any());
    }

    @Test
    void buscarOuCriar_deveCriarNovoLivro_quandoGoogleConfirmaQueExiste() {
        when(livroRepository.findByGoogleBooksId("gb-2")).thenReturn(Optional.empty());
        when(googleBooksClient.buscarPorId("gb-2")).thenReturn(Optional.of(
                new LivroEncontrado(
                        "gb-2", "Grande Sertão: Veredas", "Guimarães Rosa", "Regionalismo", "https://capa", null)));
        when(livroRepository.save(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro resultado = livroService.buscarOuCriar("gb-2");

        assertThat(resultado.getGoogleBooksId()).isEqualTo("gb-2");
        assertThat(resultado.getTitulo()).isEqualTo("Grande Sertão: Veredas");
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void buscarOuCriar_deveLancarExcecao_quandoGoogleNaoConfirmaQueLivroExiste() {
        when(livroRepository.findByGoogleBooksId("id-inventado")).thenReturn(Optional.empty());
        when(googleBooksClient.buscarPorId("id-inventado")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarOuCriar("id-inventado"))
                .isInstanceOf(LivroInvalidoException.class);

        verify(livroRepository, never()).save(any());
    }

    @Test
    void listarGeneros_deveRetornarOrdenadoAlfabeticamente() {
        when(livroRepository.listarGenerosDistintos()).thenReturn(List.of("Romance", "Aventura", "Ficção"));

        var resultado = livroService.listarGeneros();

        assertThat(resultado).containsExactly("Aventura", "Ficção", "Romance");
    }
}
