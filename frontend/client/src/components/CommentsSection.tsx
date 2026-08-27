import { useState, useEffect } from 'react';
import { useLocation } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import ReactionPicker from './ReactionPicker';
import { MessageSquare, Trash2, Edit2, Check, X, Smile } from 'lucide-react';
import { API_BASE_URL, type CommentApi } from '@/lib/api';
import { paraComentario } from '@/lib/mapeadores';
import type { Comentario } from '@/types/dominio';

interface CommentsSectionProps {
  resenhaId: number;
  onComentarioAdicionado?: () => void;
}

export default function CommentsSection({ resenhaId, onComentarioAdicionado }: CommentsSectionProps) {
  const [, setLocation] = useLocation();
  const { user, isAuthenticated } = useAuth();
  const [comentarios, setComentarios] = useState<Comentario[]>([]);
  const [novoComentario, setNovoComentario] = useState('');
  const [loading, setLoading] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');
  const [activeReactionPicker, setActiveReactionPicker] = useState<number | null>(null);

  useEffect(() => {
    buscarComentarios();
  }, [resenhaId]);

  const buscarComentarios = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/reviews/${resenhaId}/comments`);
      if (response.ok) {
        const data: CommentApi[] = await response.json();
        setComentarios(data.map(paraComentario));
      }
    } catch (error) {
      console.error('Failed to fetch comments:', error);
    }
  };

  const handleAdicionarComentario = async () => {
    if (!isAuthenticated || !novoComentario.trim()) {
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/reviews/${resenhaId}/comments`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ content: novoComentario }),
      });

      if (response.ok) {
        setNovoComentario('');
        buscarComentarios();
        onComentarioAdicionado?.();
      }
    } catch (error) {
      console.error('Failed to add comment:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExcluirComentario = async (comentarioId: number) => {
    if (!isAuthenticated) return;

    try {
      const response = await fetch(`${API_BASE_URL}/comments/${comentarioId}`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (response.ok) {
        buscarComentarios();
      }
    } catch (error) {
      console.error('Failed to delete comment:', error);
    }
  };

  const handleAtualizarComentario = async (comentarioId: number) => {
    if (!isAuthenticated || !editContent.trim()) return;

    try {
      const response = await fetch(`${API_BASE_URL}/comments/${comentarioId}`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ content: editContent }),
      });

      if (response.ok) {
        setEditingId(null);
        setEditContent('');
        buscarComentarios();
      }
    } catch (error) {
      console.error('Failed to update comment:', error);
    }
  };

  const handleAdicionarReacao = async (comentarioId: number, emoji: string) => {
    if (!isAuthenticated) return;

    try {
      const response = await fetch(`${API_BASE_URL}/comments/${comentarioId}/reactions`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ emoji }),
      });

      if (response.ok) {
        buscarComentarios();
      }
    } catch (error) {
      console.error('Failed to add reaction:', error);
    }
  };

  return (
    <div className="mt-8 pt-8 border-t border-border">
      <div className="flex items-center gap-2 mb-6">
        <MessageSquare className="w-5 h-5 text-accent" />
        <h3 className="text-xl font-bold text-foreground">
          Comentários ({comentarios.length})
        </h3>
      </div>

      {/* Add Comment Form */}
      {isAuthenticated ? (
        <div className="mb-6 p-4 bg-muted rounded-lg">
          <textarea
            value={novoComentario}
            onChange={(e) => setNovoComentario(e.target.value)}
            placeholder="Compartilhe sua opinião sobre esta resenha..."
            rows={3}
            className="w-full px-3 py-2 border border-border rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent mb-3"
          />
          <Button
            onClick={handleAdicionarComentario}
            disabled={loading || !novoComentario.trim()}
            className="bg-accent text-accent-foreground hover:bg-accent/90"
          >
            Comentar
          </Button>
        </div>
      ) : (
        <div className="mb-6 p-4 bg-muted rounded-lg text-center text-muted-foreground">
          Faça login para comentar
        </div>
      )}

      {/* Comments List */}
      <div className="space-y-4">
        {comentarios.length === 0 ? (
          <p className="text-muted-foreground text-center py-8">
            Nenhum comentário ainda. Seja o primeiro a comentar!
          </p>
        ) : (
          comentarios.map((comentario) => (
            <div key={comentario.id} className="p-4 bg-card border border-border rounded-lg">
              <div className="flex justify-between items-start mb-2">
                <div
                  className="flex items-center gap-3 w-fit hover:opacity-80"
                  onClick={() => setLocation(`/users/${comentario.usuarioId}`)}
                >
                  <Avatar>
                    <AvatarImage src={comentario.avatarUsuario} alt={comentario.nomeUsuario} />
                    <AvatarFallback>{comentario.nomeUsuario.charAt(0).toUpperCase()}</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="font-semibold text-foreground">{comentario.nomeUsuario}</p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(comentario.criadoEm).toLocaleDateString('pt-BR')}
                      {comentario.atualizadoEm && ' (editado)'}
                    </p>
                  </div>
                </div>
                {isAuthenticated && user?.id === comentario.usuarioId && (
                  <div className="flex gap-2">
                    {editingId === comentario.id ? (
                      <>
                        <button
                          onClick={() => handleAtualizarComentario(comentario.id)}
                          className="p-1 text-green-600 hover:bg-green-50 rounded"
                        >
                          <Check className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => {
                            setEditingId(null);
                            setEditContent('');
                          }}
                          className="p-1 text-red-600 hover:bg-red-50 rounded"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          onClick={() => {
                            setEditingId(comentario.id);
                            setEditContent(comentario.conteudo);
                          }}
                          className="p-1 text-accent hover:bg-accent/10 rounded"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleExcluirComentario(comentario.id)}
                          className="p-1 text-red-600 hover:bg-red-50 rounded"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </>
                    )}
                  </div>
                )}
              </div>

              {editingId === comentario.id ? (
                <textarea
                  value={editContent}
                  onChange={(e) => setEditContent(e.target.value)}
                  rows={3}
                  className="w-full px-3 py-2 border border-border rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent"
                />
              ) : (
                <p className="text-foreground leading-relaxed mb-3">{comentario.conteudo}</p>
              )}

              {/* Reactions Section */}
              <div className="flex items-center gap-2 flex-wrap mt-3 pt-3 border-t border-border">
                {comentario.reacoes.length > 0 && (
                  <div className="flex gap-2 flex-wrap">
                    {comentario.reacoes.map((reacao) => (
                      <button
                        key={reacao.emoji}
                        onClick={() => {
                          if (isAuthenticated) {
                            handleAdicionarReacao(comentario.id, reacao.emoji);
                          }
                        }}
                        className={`flex items-center gap-1 px-2 py-1 rounded-full text-sm transition-colors ${
                          reacao.userIds.includes(user?.id || 0)
                            ? 'bg-accent/20 text-accent'
                            : 'bg-muted text-foreground hover:bg-muted/80'
                        }`}
                        title={`${reacao.count} ${reacao.emoji}`}
                      >
                        <span>{reacao.emoji}</span>
                        <span className="text-xs">{reacao.count}</span>
                      </button>
                    ))}
                  </div>
                )}

                {isAuthenticated && (
                  <div className="relative">
                    <button
                      onClick={() =>
                        setActiveReactionPicker(
                          activeReactionPicker === comentario.id ? null : comentario.id
                        )
                      }
                      className="flex items-center gap-1 px-2 py-1 rounded-full text-sm bg-muted text-foreground hover:bg-muted/80 transition-colors"
                    >
                      <Smile className="w-4 h-4" />
                    </button>
                    {activeReactionPicker === comentario.id && (
                      <ReactionPicker
                        isOpen={activeReactionPicker === comentario.id}
                        onClose={() => setActiveReactionPicker(null)}
                        onSelecionarEmoji={(emoji) => handleAdicionarReacao(comentario.id, emoji)}
                      />
                    )}
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
