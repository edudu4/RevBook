package com.revbook.reviewservice.service;

import com.revbook.reviewservice.dto.UserStatsResponse;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstatisticasService {

    private final ResenhaRepository resenhaRepository;
    private final ComentarioRepository comentarioRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public EstatisticasService(
            ResenhaRepository resenhaRepository,
            ComentarioRepository comentarioRepository,
            AvaliacaoRepository avaliacaoRepository) {
        this.resenhaRepository = resenhaRepository;
        this.comentarioRepository = comentarioRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public UserStatsResponse calcular(Long usuarioId) {
        long totalResenhas = resenhaRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).size();
        long totalComentarios = comentarioRepository.countByUsuarioId(usuarioId);
        long totalAvaliacoesRecebidas = avaliacaoRepository.countByResenha_UsuarioId(usuarioId);

        return new UserStatsResponse(totalResenhas, totalComentarios, totalAvaliacoesRecebidas);
    }
}
