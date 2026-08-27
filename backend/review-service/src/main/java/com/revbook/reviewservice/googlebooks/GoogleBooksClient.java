package com.revbook.reviewservice.googlebooks;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GoogleBooksClient {

    private static final Logger log = LoggerFactory.getLogger(GoogleBooksClient.class);
    private static final int MAX_RESULTADOS = 20;

    private final RestClient restClient;
    private final String apiKey;

    public GoogleBooksClient(@Value("${revbook.google-books.api-key:}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
        this.apiKey = apiKey;
    }

    public List<LivroEncontrado> buscar(String termo) {
        if (!StringUtils.hasText(termo)) {
            return List.of();
        }

        GoogleBooksApiResponse resposta;
        try {
            resposta = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/volumes")
                                .queryParam("q", termo)
                                .queryParam("maxResults", MAX_RESULTADOS);
                        if (StringUtils.hasText(apiKey)) {
                            builder.queryParam("key", apiKey);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(GoogleBooksApiResponse.class);
        } catch (RestClientException ex) {
            log.warn("Falha ao consultar a Google Books API para o termo '{}': {}", termo, ex.getMessage());
            return List.of();
        }

        if (resposta == null || resposta.items() == null) {
            return List.of();
        }

        return resposta.items().stream()
                .map(this::paraLivroEncontrado)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Optional<LivroEncontrado> buscarPorId(String googleBooksId) {
        if (!StringUtils.hasText(googleBooksId)) {
            return Optional.empty();
        }

        try {
            GoogleBooksApiResponse.Item item = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/volumes/{id}");
                        if (StringUtils.hasText(apiKey)) {
                            builder.queryParam("key", apiKey);
                        }
                        return builder.build(googleBooksId);
                    })
                    .retrieve()
                    .body(GoogleBooksApiResponse.Item.class);

            return item != null ? paraLivroEncontrado(item) : Optional.empty();
        } catch (RestClientException ex) {
            log.warn("Falha ao verificar o livro '{}' na Google Books API: {}", googleBooksId, ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<LivroEncontrado> paraLivroEncontrado(GoogleBooksApiResponse.Item item) {
        var info = item.volumeInfo();
        if (info == null || !StringUtils.hasText(info.title())) {
            return Optional.empty();
        }

        String autor = info.authors() != null && !info.authors().isEmpty()
                ? String.join(", ", info.authors())
                : "Autor desconhecido";
        String genero = info.categories() != null && !info.categories().isEmpty()
                ? info.categories().get(0)
                : null;
        String capaUrl = info.imageLinks() != null ? paraHttps(info.imageLinks().thumbnail()) : null;

        return Optional.of(new LivroEncontrado(item.id(), info.title(), autor, genero, capaUrl));
    }

    private String paraHttps(String url) {
        return url != null ? url.replaceFirst("^http://", "https://") : null;
    }
}
