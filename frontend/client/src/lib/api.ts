/**
 * Formato bruto que trafega na rede (JSON do Gateway) — em inglês, pois é o
 * contrato público exposto pelo backend e consumido por `fetch`. Nada aqui é
 * usado fora da camada de acesso a dados; os componentes só enxergam os tipos
 * de domínio em `types/dominio.ts`, convertidos por `lib/mapeadores.ts`.
 */

/** Em produção, defina VITE_API_BASE_URL no build (aponta pro Gateway real). */
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3001';

export interface UserApi {
  id: number;
  email: string;
  name: string;
  avatar?: string;
}

export interface LoginApiResponse {
  access_token: string;
  user: UserApi;
}

export interface RatingApi {
  id: number;
  userId: number;
  value: number;
}

export interface ReactionApi {
  emoji: string;
  count: number;
  userIds: number[];
}

export interface CommentApi {
  id: number;
  content: string;
  userId: number;
  userName: string;
  userAvatar: string;
  createdAt: string;
  updatedAt?: string;
  reactions?: ReactionApi[];
}

export interface CommentWithReviewApi extends CommentApi {
  review: {
    id: number;
    bookTitle: string;
  };
}

export interface ReviewApi {
  id: number;
  bookTitle: string;
  author: string;
  genre?: string;
  coverUrl?: string;
  content: string;
  userId: number;
  userName: string;
  userAvatar?: string;
  createdAt: string;
  updatedAt?: string;
  ratings?: RatingApi[];
  comments?: CommentApi[];
}

export interface BookSearchResultApi {
  googleBooksId: string;
  title: string;
  author: string;
  genre?: string;
  coverUrl?: string;
}

export interface CreateReviewApiRequest {
  googleBooksId: string;
  bookTitle: string;
  author: string;
  genre?: string;
  coverUrl?: string;
  content: string;
}

export interface UserStatsApi {
  reviewCount: number;
  commentCount: number;
  totalRatingsReceived: number;
}
