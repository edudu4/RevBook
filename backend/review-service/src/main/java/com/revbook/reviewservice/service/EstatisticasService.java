package com.revbook.reviewservice.service;

import com.revbook.reviewservice.domain.Comentario;
import com.revbook.reviewservice.domain.Resenha;
import com.revbook.reviewservice.dto.UserStatsResponse;
import com.revbook.reviewservice.repository.AvaliacaoRepository;
import com.revbook.reviewservice.repository.ComentarioRepository;
import com.revbook.reviewservice.repository.ResenhaRepository;
import java.util.List;
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
        List<Resenha> resenhas = resenhaRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
        long totalComentarios = comentarioRepository.countByUsuarioId(usuarioId);
        long totalAvaliacoesRecebidas = avaliacaoRepository.countByResenha_UsuarioId(usuarioId);

        String nome = null;
        String avatar = null;
        if (!resenhas.isEmpty()) {
            nome = resenhas.get(0).getNomeUsuario();
            avatar = resenhas.get(0).getAvatarUsuario();
        } else {
            List<Comentario> comentarios = comentarioRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
            if (!comentarios.isEmpty()) {
                nome = comentarios.get(0).getNomeUsuario();
                avatar = comentarios.get(0).getAvatarUsuario();
            }
        }

        return new UserStatsResponse(resenhas.size(), totalComentarios, totalAvaliacoesRecebidas, nome, avatar);
    }
}
