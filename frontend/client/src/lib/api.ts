export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3001';

export interface UserApi {
  id: number;
  email: string;
  name: string;
  avatar?: string;
}

export type LoginApiResponse = UserApi;

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
  userName?: string;
  userAvatar?: string;
}
