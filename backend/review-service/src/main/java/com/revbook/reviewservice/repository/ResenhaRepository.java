package com.revbook.reviewservice.repository;

import com.revbook.reviewservice.domain.Resenha;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResenhaRepository extends JpaRepository<Resenha, Long>, JpaSpecificationExecutor<Resenha> {

    List<Resenha> findAllByOrderByCriadoEmDesc();

    List<Resenha> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);
}
