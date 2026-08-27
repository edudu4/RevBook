package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Livro;
import com.revbook.reviewservice.domain.Resenha;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ResenhaSpecifications {

    private ResenhaSpecifications() {
    }

    public static Specification<Resenha> comFiltros(String titulo, String autor, String genero) {
        return (root, query, cb) -> {
            var predicado = cb.conjunction();

            if (!StringUtils.hasText(titulo) && !StringUtils.hasText(autor) && !StringUtils.hasText(genero)) {
                return predicado;
            }

            var livro = root.<Resenha, Livro>join("livro");

            if (StringUtils.hasText(titulo)) {
                predicado = cb.and(predicado, cb.like(cb.lower(livro.get("titulo")), "%" + titulo.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(autor)) {
                predicado = cb.and(predicado, cb.like(cb.lower(livro.get("autor")), "%" + autor.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(genero)) {
                predicado = cb.and(predicado, cb.like(cb.lower(livro.get("genero")), "%" + genero.toLowerCase() + "%"));
            }

            return predicado;
        };
    }
}
