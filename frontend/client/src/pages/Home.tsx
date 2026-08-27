import { useCallback, useEffect, useRef, useState } from 'react';
import { useLocation } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import Footer from '@/components/Footer';
import Spinner from '@/components/Spinner';
import SearchBar from '@/components/SearchBar';
import BuscaLivro from '@/components/BuscaLivro';
import AvaliacaoEstrelas from '@/components/AvaliacaoEstrelas';
import { LogOut, Plus } from 'lucide-react';
import { API_BASE_URL, type CreateReviewApiRequest, type ReviewApi } from '@/lib/api';
import { paraResenha } from '@/lib/mapeadores';
import { preloadImagens } from '@/lib/preloadImagens';
import type { FiltrosBusca, LivroEncontrado, Resenha } from '@/types/dominio';

const TAMANHO_PAGINA = 10;

export default function Home() {
  const [, setLocation] = useLocation();
  const { user, logout, isAuthenticated } = useAuth();
  const [resenhas, setResenhas] = useState<Resenha[]>([]);
  const [loading, setLoading] = useState(true);
  const [carregandoMais, setCarregandoMais] = useState(false);
  const [temMais, setTemMais] = useState(true);
  const [showNewReviewModal, setShowNewReviewModal] = useState(false);
  const [livroSelecionado, setLivroSelecionado] = useState<LivroEncontrado | null>(null);
  const [conteudoResenha, setConteudoResenha] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  const paginaRef = useRef(0);
  const carregandoRef = useRef(false);
  const temMaisRef = useRef(true);
  const filtrosAtivosRef = useRef<FiltrosBusca | null>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);

  const montarUrlPagina = (pagina: number, filtros: FiltrosBusca | null) => {
    const params = new URLSearchParams();
    if (filtros?.titulo) params.append('title', filtros.titulo);
    if (filtros?.autor) params.append('author', filtros.autor);
    if (filtros?.genero) params.append('genre', filtros.genero);
    params.append('page', String(pagina));
    params.append('size', String(TAMANHO_PAGINA));
    const base = filtros ? `${API_BASE_URL}/reviews/search` : `${API_BASE_URL}/reviews`;
    return `${base}?${params}`;
  };

  useEffect(() => {
    buscarResenhas();
  }, []);

  const buscarResenhas = async () => {
    try {
      setLoading(true);
      filtrosAtivosRef.current = null;
      const response = await fetch(montarUrlPagina(0, null));
      if (response.ok) {
        const data: ReviewApi[] = await response.json();
        const mapeadas = data.map(paraResenha);
        await preloadImagens(mapeadas.flatMap((r) => [r.capaUrl, r.avatarUsuario]));
        setResenhas(mapeadas);
        paginaRef.current = 1;
        temMaisRef.current = mapeadas.length === TAMANHO_PAGINA;
        setTemMais(temMaisRef.current);
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
      filtrosAtivosRef.current = filtros;

      const response = await fetch(montarUrlPagina(0, filtros));
      if (response.ok) {
        const data: ReviewApi[] = await response.json();
        const mapeadas = data.map(paraResenha);
        await preloadImagens(mapeadas.flatMap((r) => [r.capaUrl, r.avatarUsuario]));
        setResenhas(mapeadas);
        paginaRef.current = 1;
        temMaisRef.current = mapeadas.length === TAMANHO_PAGINA;
        setTemMais(temMaisRef.current);
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

  const carregarMais = async () => {
    if (carregandoRef.current || !temMaisRef.current) return;

    carregandoRef.current = true;
    setCarregandoMais(true);
    try {
      const response = await fetch(montarUrlPagina(paginaRef.current, filtrosAtivosRef.current));
      if (response.ok) {
        const data: ReviewApi[] = await response.json();
        const mapeadas = data.map(paraResenha);
        await preloadImagens(mapeadas.flatMap((r) => [r.capaUrl, r.avatarUsuario]));
        setResenhas((atual) => [...atual, ...mapeadas]);
        paginaRef.current += 1;
        temMaisRef.current = mapeadas.length === TAMANHO_PAGINA;
        setTemMais(temMaisRef.current);
      }
    } catch (error) {
      console.error('Failed to load more reviews:', error);
    } finally {
      carregandoRef.current = false;
      setCarregandoMais(false);
    }
  };

  const sentinelaRef = useCallback((node: HTMLDivElement | null) => {
    observerRef.current?.disconnect();

    if (node) {
      observerRef.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            carregarMais();
          }
        },
        { rootMargin: '300px' }
      );
      observerRef.current.observe(node);
    }
  }, []);

  const handleCriarResenha = async () => {
    if (!isAuthenticated || !livroSelecionado || !conteudoResenha) {
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
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
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
    if (!isAuthenticated) {
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
        body: JSON.stringify({ reviewId: resenhaId, value: valor }),
      });
      buscarResenhas();
    } catch (error) {
      console.error('Failed to rate review:', error);
    }
  };

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header */}
      <header className="sticky top-0 z-20 bg-card border-b border-border">
        <div className="max-w-7xl mx-auto px-4 py-4 sm:py-6 flex flex-wrap gap-y-3 justify-between items-center">
          <div className="flex items-center gap-2 sm:gap-3 min-w-0">
            <span
              className="flex items-center justify-center w-8 h-8 sm:w-9 sm:h-9 rounded-md font-bold text-base sm:text-lg flex-shrink-0"
              style={{ backgroundColor: '#542229', color: '#D5A62A' }}
              aria-hidden="true"
            >
              R
            </span>
            <div className="min-w-0">
              <h1 className="text-xl sm:text-3xl font-bold text-foreground leading-tight truncate">RevBook</h1>
              <p className="hidden sm:block text-xs text-muted-foreground">Resenhas de livros, por quem lê de verdade.</p>
            </div>
          </div>

          {isAuthenticated ? (
            <div className="flex items-center gap-2 sm:gap-3">
              <Button
                onClick={() => setShowNewReviewModal(true)}
                className="btn-nova-resenha rounded-full px-3 sm:px-6 font-semibold text-accent-foreground border-0"
              >
                <Plus className="w-4 h-4 sm:mr-2" />
                <span className="hidden sm:inline">Nova Resenha</span>
              </Button>
              <div className="flex items-center gap-2 sm:gap-3 border-l border-border pl-2 sm:pl-4">
                <span className="hidden sm:inline text-sm text-muted-foreground">{user?.nome}</span>
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
            </div>
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
      <main className="max-w-7xl mx-auto px-4 py-8 sm:py-12">
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
        {loading && <Spinner label="Carregando resenhas..." />}

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
              className="bg-card border border-border rounded-lg p-4 sm:p-6 hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => setLocation(`/reviews/${resenha.id}`)}
            >
              <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-2 mb-4">
                <div className="flex gap-4 min-w-0">
                  {resenha.capaUrl && (
                    <img
                      src={resenha.capaUrl}
                      alt={resenha.titulo}
                      loading="lazy"
                      decoding="async"
                      className="w-16 h-24 object-cover rounded flex-shrink-0"
                    />
                  )}
                  <div className="min-w-0">
                    <h2 className="text-lg sm:text-2xl font-bold text-foreground mb-1">
                      {resenha.titulo}
                    </h2>
                    <div className="flex flex-wrap items-center gap-2 sm:gap-4 text-muted-foreground">
                      <span>por {resenha.autor}</span>
                      {resenha.genero && (
                        <span className="text-xs bg-accent/10 text-accent px-2 py-1 rounded">
                          {resenha.genero}
                        </span>
                      )}
                    </div>
                    <div
                      className="flex items-center gap-2 mt-2 w-fit hover:opacity-80"
                      onClick={(e) => {
                        e.stopPropagation();
                        setLocation(`/users/${resenha.usuarioId}`);
                      }}
                    >
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
                <span className="text-sm text-muted-foreground flex-shrink-0">
                  {new Date(resenha.criadoEm).toLocaleDateString('pt-BR')}
                  {resenha.atualizadoEm && ' (editado)'}
                </span>
              </div>

              <p className="text-foreground leading-relaxed mb-4 line-clamp-3">
                {resenha.conteudo}
              </p>

              <div
                className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 pt-4 border-t border-border"
                onClick={(e) => e.stopPropagation()}
              >
                <div className="flex flex-wrap items-center gap-2 sm:gap-4 text-sm text-muted-foreground">
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
                  <span>{resenha.comentarios.length} comentários</span>
                </div>
                <Button
                  onClick={() => setLocation(`/reviews/${resenha.id}`)}
                  variant="ghost"
                  size="sm"
                  className="text-accent hover:text-accent/80 self-start sm:self-auto"
                >
                  Ver Resenha Completa
                </Button>
              </div>
            </article>
          ))}
        </div>

        {/* Infinite Scroll Sentinel */}
        <div ref={sentinelaRef} className={resenhas.length > 0 ? 'py-8 text-center' : ''}>
          {carregandoMais && <Spinner label="Carregando mais resenhas..." tamanho="pequeno" />}
          {!loading && !temMais && !carregandoMais && resenhas.length > 0 && (
            <p className="text-muted-foreground text-sm">Você chegou ao fim das resenhas.</p>
          )}
        </div>
      </main>

      <Footer />

      {/* New Review Modal */}
      {showNewReviewModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-card rounded-lg p-4 sm:p-8 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
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
                disabled={!livroSelecionado || !conteudoResenha.trim()}
                className="flex-1 bg-accent text-accent-foreground hover:bg-accent/90 disabled:opacity-50 disabled:cursor-not-allowed"
                title={!livroSelecionado ? 'Selecione um livro da busca para publicar' : undefined}
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
