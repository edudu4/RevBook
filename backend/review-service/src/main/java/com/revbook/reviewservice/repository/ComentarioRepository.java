package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Comentario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByResenha_IdOrderByCriadoEmDesc(Long resenhaId);

    List<Comentario> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    long countByUsuarioId(Long usuarioId);
}
