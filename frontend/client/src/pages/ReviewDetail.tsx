import { useEffect, useState } from 'react';
import { useLocation, useRoute } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import AvaliacaoEstrelas from '@/components/AvaliacaoEstrelas';
import CommentsSection from '@/components/CommentsSection';
import Footer from '@/components/Footer';
import Spinner from '@/components/Spinner';
import { ArrowLeft, Edit2, Trash2, Check, X } from 'lucide-react';
import { API_BASE_URL, type ReviewApi } from '@/lib/api';
import { paraResenha } from '@/lib/mapeadores';
import { preloadImagens } from '@/lib/preloadImagens';
import type { Resenha } from '@/types/dominio';

export default function ReviewDetail() {
  const [, params] = useRoute('/reviews/:id');
  const [, setLocation] = useLocation();
  const { user, isAuthenticated } = useAuth();
  const [resenha, setResenha] = useState<Resenha | null>(null);
  const [loading, setLoading] = useState(true);
  const [naoEncontrada, setNaoEncontrada] = useState(false);
  const [editando, setEditando] = useState(false);
  const [conteudoEdicao, setConteudoEdicao] = useState('');

  const resenhaId = params?.id;

  useEffect(() => {
    if (resenhaId) {
      buscarResenha(resenhaId);
    }
  }, [resenhaId]);

  const buscarResenha = async (id: string) => {
    try {
      setLoading(true);
      setNaoEncontrada(false);
      const response = await fetch(`${API_BASE_URL}/reviews/${id}`);
      if (response.ok) {
        const data: ReviewApi = await response.json();
        const mapeada = paraResenha(data);
        await preloadImagens([mapeada.capaUrl, mapeada.avatarUsuario]);
        setResenha(mapeada);
      } else {
        setNaoEncontrada(true);
      }
    } catch (error) {
      console.error('Failed to fetch review:', error);
      setNaoEncontrada(true);
    } finally {
      setLoading(false);
    }
  };

  const handleAvaliar = async (valor: number) => {
    if (!isAuthenticated || !resenhaId) {
      setLocation('/login');
      return;
    }

    try {
      await fetch(`${API_BASE_URL}/reviews/rate`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ reviewId: Number(resenhaId), value: valor }),
      });
      buscarResenha(resenhaId);
    } catch (error) {
      console.error('Failed to rate review:', error);
    }
  };

  const handleAtualizar = async () => {
    if (!isAuthenticated || !resenhaId || !conteudoEdicao.trim()) return;

    try {
      const response = await fetch(`${API_BASE_URL}/reviews/${resenhaId}`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ content: conteudoEdicao }),
      });

      if (response.ok) {
        setEditando(false);
        buscarResenha(resenhaId);
      }
    } catch (error) {
      console.error('Failed to update review:', error);
    }
  };

  const handleExcluir = async () => {
    if (!isAuthenticated || !resenhaId) return;
    if (!confirm('Tem certeza que deseja excluir esta resenha? Essa ação não pode ser desfeita.')) return;

    try {
      const response = await fetch(`${API_BASE_URL}/reviews/${resenhaId}`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (response.ok) {
        setLocation('/');
      }
    } catch (error) {
      console.error('Failed to delete review:', error);
    }
  };

  if (loading) {
    return <Spinner label="Carregando resenha..." telaCheia />;
  }

  if (naoEncontrada || !resenha) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center gap-4">
        <p className="text-muted-foreground">Resenha não encontrada.</p>
        <Button onClick={() => setLocation('/')} variant="outline">
          Voltar ao Feed
        </Button>
      </div>
    );
  }

  const media = resenha.avaliacoes.length
    ? resenha.avaliacoes.reduce((soma, a) => soma + a.valor, 0) / resenha.avaliacoes.length
    : 0;
  const ehDono = isAuthenticated && user?.id === resenha.usuarioId;

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <header className="bg-card border-b border-border">
        <div className="max-w-3xl mx-auto px-4 py-6 flex items-center gap-4">
          <Button onClick={() => setLocation('/')} variant="ghost" size="sm">
            <ArrowLeft className="w-4 h-4" />
          </Button>
          <h1 className="text-xl font-bold text-foreground">Resenha</h1>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 py-12">
        <article className="bg-card border border-border rounded-lg p-8">
          <div className="flex gap-6 mb-6">
            {resenha.capaUrl && (
              <img
                src={resenha.capaUrl}
                alt={resenha.titulo}
                decoding="async"
                className="w-24 h-36 object-cover rounded flex-shrink-0"
              />
            )}
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">{resenha.titulo}</h2>
              <div className="flex items-center gap-4 text-muted-foreground mb-3">
                <span>por {resenha.autor}</span>
                {resenha.genero && (
                  <span className="text-xs bg-accent/10 text-accent px-2 py-1 rounded">{resenha.genero}</span>
                )}
              </div>
              <AvaliacaoEstrelas
                media={media}
                total={resenha.avaliacoes.length}
                interativo={isAuthenticated}
                onAvaliar={handleAvaliar}
              />
            </div>
          </div>

          <div className="flex items-center justify-between mb-6 pb-6 border-b border-border">
            <div
              className="flex items-center gap-3 w-fit hover:opacity-80"
              onClick={() => setLocation(`/users/${resenha.usuarioId}`)}
            >
              <Avatar>
                <AvatarImage src={resenha.avatarUsuario} alt={resenha.nomeUsuario} />
                <AvatarFallback>{resenha.nomeUsuario.charAt(0).toUpperCase()}</AvatarFallback>
              </Avatar>
              <div>
                <p className="font-semibold text-foreground">{resenha.nomeUsuario}</p>
                <p className="text-sm text-muted-foreground">
                  {new Date(resenha.criadoEm).toLocaleDateString('pt-BR')}
                  {resenha.atualizadoEm && ' (editado)'}
                </p>
              </div>
            </div>

            {ehDono && !editando && (
              <div className="flex gap-1">
                <button
                  onClick={() => {
                    setEditando(true);
                    setConteudoEdicao(resenha.conteudo);
                  }}
                  className="p-2 text-accent hover:bg-accent/10 rounded"
                  title="Editar resenha"
                >
                  <Edit2 className="w-4 h-4" />
                </button>
                <button
                  onClick={handleExcluir}
                  className="p-2 text-red-600 hover:bg-red-50 rounded"
                  title="Excluir resenha"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            )}
          </div>

          {editando ? (
            <div>
              <textarea
                value={conteudoEdicao}
                onChange={(e) => setConteudoEdicao(e.target.value)}
                rows={8}
                className="w-full px-3 py-2 border border-border rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent"
              />
              <div className="flex gap-2 mt-3">
                <Button onClick={handleAtualizar} size="sm" className="bg-accent text-accent-foreground hover:bg-accent/90">
                  <Check className="w-4 h-4 mr-1" />
                  Salvar
                </Button>
                <Button onClick={() => setEditando(false)} variant="outline" size="sm">
                  <X className="w-4 h-4 mr-1" />
                  Cancelar
                </Button>
              </div>
            </div>
          ) : (
            <p className="text-foreground leading-relaxed whitespace-pre-wrap">{resenha.conteudo}</p>
          )}

          <CommentsSection resenhaId={resenha.id} />
        </article>
      </main>

      <Footer />
    </div>
  );
}
