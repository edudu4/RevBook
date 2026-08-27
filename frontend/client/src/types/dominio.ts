export interface Usuario {
  id: number;
  email: string;
  nome: string;
  avatar?: string;
}

export interface Avaliacao {
  id: number;
  usuarioId: number;
  valor: number;
}

export interface Reacao {
  emoji: string;
  count: number;
  userIds: number[];
}

export interface Comentario {
  id: number;
  conteudo: string;
  usuarioId: number;
  nomeUsuario: string;
  avatarUsuario: string;
  criadoEm: string;
  atualizadoEm?: string;
  reacoes: Reacao[];
}

export interface ComentarioComResenha extends Comentario {
  resenha: {
    id: number;
    titulo: string;
  };
}

export interface Resenha {
  id: number;
  titulo: string;
  autor: string;
  genero?: string;
  capaUrl?: string;
  conteudo: string;
  usuarioId: number;
  nomeUsuario: string;
  avatarUsuario?: string;
  criadoEm: string;
  atualizadoEm?: string;
  avaliacoes: Avaliacao[];
  comentarios: Comentario[];
}

export interface LivroEncontrado {
  googleBooksId: string;
  titulo: string;
  autor: string;
  genero?: string;
  capaUrl?: string;
}

export interface EstatisticasUsuario {
  totalResenhas: number;
  totalComentarios: number;
  totalAvaliacoesRecebidas: number;
}

export interface FiltrosBusca {
  titulo?: string;
  autor?: string;
  genero?: string;
}
