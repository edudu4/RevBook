package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Reacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReacaoRepository extends JpaRepository<Reacao, Long> {

    Optional<Reacao> findByComentario_IdAndUsuarioIdAndEmoji(Long comentarioId, Long usuarioId, String emoji);

    List<Reacao> findByComentario_Id(Long comentarioId);
}
