import type {
  BookSearchResultApi,
  CommentApi,
  CommentWithReviewApi,
  NotificationApi,
  ReactionApi,
  ReviewApi,
  UserApi,
  UserStatsApi,
} from './api';
import type {
  Comentario,
  ComentarioComResenha,
  EstatisticasUsuario,
  LivroEncontrado,
  Notificacao,
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
    parentId: api.parentId,
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
    capaUrl: api.coverUrl,
    sinopse: api.synopsis,
    conteudo: api.content,
    usuarioId: api.userId,
    nomeUsuario: api.userName,
    avatarUsuario: api.userAvatar,
    criadoEm: api.createdAt,
    atualizadoEm: api.updatedAt,
    avaliacoes: (api.ratings ?? []).map((r) => ({ id: r.id, usuarioId: r.userId, valor: r.value })),
    comentarios: (api.comments ?? []).map(paraComentario),
  };
}

export function paraLivroEncontrado(api: BookSearchResultApi): LivroEncontrado {
  return {
    googleBooksId: api.googleBooksId,
    titulo: api.title,
    autor: api.author,
    genero: api.genre,
    capaUrl: api.coverUrl,
    sinopse: api.synopsis,
  };
}

export function paraEstatisticas(api: UserStatsApi): EstatisticasUsuario {
  return {
    totalResenhas: api.reviewCount,
    totalComentarios: api.commentCount,
    totalAvaliacoesRecebidas: api.totalRatingsReceived,
    nomeUsuario: api.userName,
    avatarUsuario: api.userAvatar,
  };
}

export function paraNotificacao(api: NotificationApi): Notificacao {
  return {
    id: api.id,
    tipo: api.type,
    resenhaId: api.reviewId,
    tituloLivro: api.bookTitle,
    nomeAtor: api.actorName,
    avatarAtor: api.actorAvatar,
    lida: api.read,
    criadoEm: api.createdAt,
  };
}
