/**
 * Traduz o contrato bruto da API (inglês, `lib/api.ts`) para o modelo de
 * domínio (português, `types/dominio.ts`) — o mesmo papel que os DTOs de
 * resposta cumprem no Gateway em Spring.
 */
import type {
  CommentApi,
  CommentWithReviewApi,
  ReactionApi,
  ReviewApi,
  UserApi,
  UserStatsApi,
} from './api';
import type {
  Comentario,
  ComentarioComResenha,
  EstatisticasUsuario,
  Reacao,
  Resenha,
  Usuario,
} from '@/types/dominio';

export function paraUsuario(api: UserApi): Usuario {
  return {
    id: api.id,
    email: api.email,
    nome: api.name,
    avatar: api.avatar,
  };
}

export function paraReacao(api: ReactionApi): Reacao {
  return {
    emoji: api.emoji,
    count: api.count,
    userIds: api.userIds,
  };
}

export function paraComentario(api: CommentApi): Comentario {
  return {
    id: api.id,
    conteudo: api.content,
    usuarioId: api.userId,
    nomeUsuario: api.userName,
    avatarUsuario: api.userAvatar,
    criadoEm: api.createdAt,
    atualizadoEm: api.updatedAt,
    reacoes: (api.reactions ?? []).map(paraReacao),
  };
}

export function paraComentarioComResenha(api: CommentWithReviewApi): ComentarioComResenha {
  return {
    ...paraComentario(api),
    resenha: { id: api.review.id, titulo: api.review.bookTitle },
  };
}

export function paraResenha(api: ReviewApi): Resenha {
  return {
    id: api.id,
    titulo: api.bookTitle,
    autor: api.author,
    genero: api.genre,
    conteudo: api.content,
    usuarioId: api.userId,
    nomeUsuario: api.userName,
    criadoEm: api.createdAt,
    avaliacoes: (api.ratings ?? []).map((r) => ({ id: r.id, usuarioId: r.userId, valor: r.value })),
    comentarios: (api.comments ?? []).map(paraComentario),
  };
}

export function paraEstatisticas(api: UserStatsApi): EstatisticasUsuario {
  return {
    totalResenhas: api.reviewCount,
    totalComentarios: api.commentCount,
    totalAvaliacoesRecebidas: api.totalRatingsReceived,
  };
}
