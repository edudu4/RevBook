package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Resenha;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Monta a busca combinável por titulo/autor/genero (equivalente ao andWhere + ILIKE
 * do QueryBuilder original em TypeORM), usando LOWER()+LIKE para funcionar de forma
 * portável no Postgres.
 */
public final class ResenhaSpecifications {

    private ResenhaSpecifications() {
    }

    public static Specification<Resenha> comFiltros(String titulo, String autor, String genero) {
        return (root, query, cb) -> {
            var predicado = cb.conjunction();

            if (StringUtils.hasText(titulo)) {
                predicado = cb.and(predicado,
                        cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(autor)) {
                predicado = cb.and(predicado,
                        cb.like(cb.lower(root.get("autor")), "%" + autor.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(genero)) {
                predicado = cb.and(predicado,
                        cb.like(cb.lower(root.get("genero")), "%" + genero.toLowerCase() + "%"));
            }

            return predicado;
        };
    }
}
