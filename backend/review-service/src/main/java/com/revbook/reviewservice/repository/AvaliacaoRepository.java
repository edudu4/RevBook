package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Avaliacao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Optional<Avaliacao> findByResenha_IdAndUsuarioId(Long resenhaId, Long usuarioId);

    long countByResenha_UsuarioId(Long usuarioId);
}
