package com.revbook.reviewservice.dto;

import com.revbook.reviewservice.domain.Reacao;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reações de um comentário agrupadas por emoji, com contagem e a lista de usuários que reagiram. */
public record ReactionResponse(String emoji, int count, List<Long> userIds) {

    public static List<ReactionResponse> agrupar(List<Reacao> reacoes) {
        Map<String, List<Long>> porEmoji = new LinkedHashMap<>();
        for (Reacao reacao : reacoes) {
            porEmoji.computeIfAbsent(reacao.getEmoji(), k -> new ArrayList<>()).add(reacao.getUsuarioId());
        }

        List<ReactionResponse> resultado = new ArrayList<>();
        porEmoji.forEach((emoji, usuarioIds) -> resultado.add(new ReactionResponse(emoji, usuarioIds.size(), usuarioIds)));
        return resultado;
    }
}
