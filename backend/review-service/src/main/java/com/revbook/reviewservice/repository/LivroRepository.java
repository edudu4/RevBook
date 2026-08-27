package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Livro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByGoogleBooksId(String googleBooksId);

    @Query("select distinct l.genero from Livro l where l.genero is not null and l.genero <> ''")
    List<String> listarGenerosDistintos();
}
