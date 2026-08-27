package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Notificacao;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);

    List<Notificacao> findByUsuarioIdAndLidaFalse(Long usuarioId);
}
