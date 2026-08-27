import { useEffect, useState } from 'react';
import { useLocation } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import CommentsSection from '@/components/CommentsSection';
import SearchBar from '@/components/SearchBar';
import BuscaLivro from '@/components/BuscaLivro';
import AvaliacaoEstrelas from '@/components/AvaliacaoEstrelas';
import { LogOut, Plus } from 'lucide-react';
import { API_BASE_URL, type CreateReviewApiRequest, type ReviewApi } from '@/lib/api';
import { paraResenha } from '@/lib/mapeadores';
import type { FiltrosBusca, LivroEncontrado, Resenha } from '@/types/dominio';

export default function Home() {
  const [, setLocation] = useLocation();
  const { user, token, logout, isAuthenticated } = useAuth();
  const [resenhas, setResenhas] = useState<Resenha[]>([]);
  const [loading, setLoading] = useState(true);
  const [showNewReviewModal, setShowNewReviewModal] = useState(false);
  const [expandedResenhaId, setExpandedResenhaId] = useState<number | null>(null);
  const [livroSelecionado, setLivroSelecionado] = useState<LivroEncontrado | null>(null);
  const [conteudoResenha, setConteudoResenha] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  useEffect(() => {
    buscarResenhas();
  }, []);

  const buscarResenhas = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/reviews`);
      if (response.ok) {
        const data: ReviewApi[] = await response.json();
        setResenhas(data.map(paraResenha));
      }
    } catch (error) {
      console.error('Failed to fetch reviews:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleBuscar = async (filtros: FiltrosBusca) => {
    try {
      setLoading(true);
      setIsSearching(true);

      const params = new URLSearchParams();
      if (filtros.titulo) params.append('title', filtros.titulo);
      if (filtros.autor) params.append('author', filtros.autor);
      if (filtros.genero) params.append('genre', filtros.genero);

      const response = await fetch(`${API_BASE_URL}/reviews/search?${params}`);
      if (response.ok) {
        const data: ReviewApi[] = await response.json();
        setResenhas(data.map(paraResenha));
      }
    } catch (error) {
      console.error('Failed to search reviews:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLimparBusca = () => {
    setIsSearching(false);
    buscarResenhas();
  };

  const handleCriarResenha = async () => {
    if (!token || !livroSelecionado || !conteudoResenha) {
      alert('Por favor, escolha um livro e escreva o conteúdo da resenha');
      return;
    }

    try {
      const corpo: CreateReviewApiRequest = {
        googleBooksId: livroSelecionado.googleBooksId,
        bookTitle: livroSelecionado.titulo,
        author: livroSelecionado.autor,
        genre: livroSelecionado.genero,
        coverUrl: livroSelecionado.capaUrl,
        content: conteudoResenha,
      };

      const response = await fetch(`${API_BASE_URL}/reviews`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(corpo),
      });

      if (response.ok) {
        setLivroSelecionado(null);
        setConteudoResenha('');
        setShowNewReviewModal(false);
        buscarResenhas();
      }
    } catch (error) {
      console.error('Failed to create review:', error);
    }
  };

  const handleAvaliarResenha = async (resenhaId: number, valor: number) => {
    if (!token) {
      setLocation('/login');
      return;
    }

    try {
      await fetch(`${API_BASE_URL}/reviews/rate`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ reviewId: resenhaId, value: valor }),
      });
      buscarResenhas();
    } catch (error) {
      console.error('Failed to rate review:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="bg-card border-b border-border">
        <div className="max-w-6xl mx-auto px-4 py-6 flex justify-between items-center">
          <div className="flex items-center gap-3">
            <h1 className="text-3xl font-bold text-foreground">RevBook</h1>
          </div>

          {isAuthenticated ? (
            <>
              <Button
                onClick={() => setShowNewReviewModal(true)}
                className="bg-accent text-accent-foreground hover:bg-accent/90"
              >
                <Plus className="w-4 h-4 mr-2" />
                Nova Resenha
              </Button>
              <div className="flex items-center gap-3 border-l border-border pl-4">
                <span className="text-sm text-muted-foreground">{user?.nome}</span>
                <Button
                  onClick={() => setLocation('/profile')}
                  variant="ghost"
                  size="sm"
                  title="Meu Perfil"
                  className="p-0 rounded-full"
                >
                  <Avatar>
                    <AvatarImage src={user?.avatar} alt={user?.nome} />
                    <AvatarFallback>{user?.nome?.charAt(0).toUpperCase()}</AvatarFallback>
                  </Avatar>
                </Button>
                <Button
                  onClick={() => {
                    logout();
                    setLocation('/login');
                  }}
                  variant="ghost"
                  size="sm"
                >
                  <LogOut className="w-4 h-4" />
                </Button>
              </div>
            </>
          ) : (
            <Button
              onClick={() => setLocation('/login')}
              className="bg-accent text-accent-foreground hover:bg-accent/90"
            >
              Entrar
            </Button>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 py-12">
        {/* Search Bar */}
        <SearchBar onSearch={handleBuscar} onClear={handleLimparBusca} />

        {/* Status */}
        {isSearching && (
          <div className="mb-6 p-4 bg-accent/10 border border-accent rounded-lg">
            <p className="text-sm text-foreground">
              Mostrando resultados da busca. {resenhas.length} resenha(s) encontrada(s).
            </p>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="text-center py-12">
            <p className="text-muted-foreground">Carregando resenhas...</p>
          </div>
        )}

        {/* Empty State */}
        {!loading && resenhas.length === 0 && (
          <div className="text-center py-12 bg-muted rounded-lg">
            <p className="text-muted-foreground mb-4">
              {isSearching ? 'Nenhuma resenha encontrada com esses critérios.' : 'Nenhuma resenha ainda'}
            </p>
            {isSearching && (
              <Button
                onClick={handleLimparBusca}
                variant="outline"
              >
                Limpar Busca
              </Button>
            )}
          </div>
        )}

        {/* Reviews List */}
        <div className="space-y-6">
          {resenhas.map((resenha) => (
            <article
              key={resenha.id}
              className="bg-card border border-border rounded-lg p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="flex gap-4">
                  {resenha.capaUrl && (
                    <img
                      src={resenha.capaUrl}
                      alt={resenha.titulo}
                      className="w-16 h-24 object-cover rounded flex-shrink-0"
                    />
                  )}
                  <div>
                    <h2 className="text-2xl font-bold text-foreground mb-1">
                      {resenha.titulo}
                    </h2>
                    <div className="flex items-center gap-4 text-muted-foreground">
                      <span>por {resenha.autor}</span>
                      {resenha.genero && (
                        <>
                          <span>•</span>
                          <span className="text-xs bg-accent/10 text-accent px-2 py-1 rounded">
                            {resenha.genero}
                          </span>
                        </>
                      )}
                    </div>
                    <div className="flex items-center gap-2 mt-2">
                      <Avatar className="size-6">
                        <AvatarImage src={resenha.avatarUsuario} alt={resenha.nomeUsuario} />
                        <AvatarFallback className="text-xs">
                          {resenha.nomeUsuario.charAt(0).toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                      <span className="text-sm text-muted-foreground">{resenha.nomeUsuario}</span>
                    </div>
                  </div>
                </div>
                <span className="text-sm text-muted-foreground">
                  {new Date(resenha.criadoEm).toLocaleDateString('pt-BR')}
                </span>
              </div>

              <p className="text-foreground leading-relaxed mb-4">
                {resenha.conteudo}
              </p>

              <div className="flex justify-between items-center pt-4 border-t border-border">
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <AvaliacaoEstrelas
                    media={
                      resenha.avaliacoes.length
                        ? resenha.avaliacoes.reduce((soma, a) => soma + a.valor, 0) / resenha.avaliacoes.length
                        : 0
                    }
                    total={resenha.avaliacoes.length}
                    interativo={isAuthenticated}
                    onAvaliar={(valor) => handleAvaliarResenha(resenha.id, valor)}
                  />
                  <span>•</span>
                  <span>{resenha.comentarios.length} comentários</span>
                </div>
                <div className="flex gap-2">
                  <Button
                    onClick={() =>
                      setExpandedResenhaId(
                        expandedResenhaId === resenha.id ? null : resenha.id
                      )
                    }
                    variant="ghost"
                    size="sm"
                    className="text-accent hover:text-accent/80"
                  >
                    {expandedResenhaId === resenha.id
                      ? 'Ocultar Comentários'
                      : 'Ver Comentários'}
                  </Button>
                </div>
              </div>

              {expandedResenhaId === resenha.id && (
                <CommentsSection
                  resenhaId={resenha.id}
                />
              )}
            </article>
          ))}
        </div>
      </main>

      {/* New Review Modal */}
      {showNewReviewModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-card rounded-lg p-8 max-w-2xl w-full mx-4">
            <h2 className="text-2xl font-bold text-foreground mb-6">
              Criar Nova Resenha
            </h2>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Livro *
                </label>
                <BuscaLivro
                  livroSelecionado={livroSelecionado}
                  onSelecionar={setLivroSelecionado}
                  onLimpar={() => setLivroSelecionado(null)}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  Resenha *
                </label>
                <textarea
                  value={conteudoResenha}
                  onChange={(e) => setConteudoResenha(e.target.value)}
                  placeholder="Escreva sua resenha..."
                  rows={6}
                  className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent resize-none"
                />
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <Button
                onClick={handleCriarResenha}
                className="flex-1 bg-accent text-accent-foreground hover:bg-accent/90"
              >
                Publicar Resenha
              </Button>
              <Button
                onClick={() => {
                  setShowNewReviewModal(false);
                  setLivroSelecionado(null);
                  setConteudoResenha('');
                }}
                variant="outline"
                className="flex-1"
              >
                Cancelar
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
