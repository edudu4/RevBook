import { useEffect, useState } from 'react';
import { useLocation } from 'wouter';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { ArrowLeft, BookOpen, MessageSquare, Award } from 'lucide-react';
import { API_BASE_URL, type CommentWithReviewApi, type ReviewApi, type UserStatsApi } from '@/lib/api';
import { paraComentarioComResenha, paraEstatisticas, paraResenha } from '@/lib/mapeadores';
import type { ComentarioComResenha, EstatisticasUsuario, Resenha } from '@/types/dominio';

export default function Profile() {
  const [, setLocation] = useLocation();
  const { user, isAuthenticated } = useAuth();
  const [resenhas, setResenhas] = useState<Resenha[]>([]);
  const [comentarios, setComentarios] = useState<ComentarioComResenha[]>([]);
  const [estatisticas, setEstatisticas] = useState<EstatisticasUsuario | null>(null);
  const [loading, setLoading] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState<'resenhas' | 'comentarios'>('resenhas');

  useEffect(() => {
    if (!isAuthenticated || !user) {
      setLocation('/login');
      return;
    }

    buscarDadosDoUsuario();
  }, [isAuthenticated, user]);

  const buscarDadosDoUsuario = async () => {
    if (!user?.id) return;

    try {
      setLoading(true);

      const statsResponse = await fetch(`${API_BASE_URL}/users/${user.id}/profile`);
      if (statsResponse.ok) {
        const statsData: UserStatsApi = await statsResponse.json();
        setEstatisticas(paraEstatisticas(statsData));
      }

      const resenhasResponse = await fetch(`${API_BASE_URL}/users/${user.id}/reviews`);
      if (resenhasResponse.ok) {
        const resenhasData: ReviewApi[] = await resenhasResponse.json();
        setResenhas(resenhasData.map(paraResenha));
      }

      const comentariosResponse = await fetch(`${API_BASE_URL}/users/${user.id}/comments`);
      if (comentariosResponse.ok) {
        const comentariosData: CommentWithReviewApi[] = await comentariosResponse.json();
        setComentarios(comentariosData.map(paraComentarioComResenha));
      }
    } catch (error) {
      console.error('Failed to fetch user data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <p className="text-muted-foreground">Carregando perfil...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="bg-card border-b border-border">
        <div className="max-w-6xl mx-auto px-4 py-6 flex items-center gap-4">
          <Button
            onClick={() => setLocation('/')}
            variant="ghost"
            size="sm"
          >
            <ArrowLeft className="w-4 h-4" />
          </Button>
          <h1 className="text-3xl font-bold text-foreground">Meu Perfil</h1>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 py-12">
        {/* Profile Header */}
        <div className="bg-card border border-border rounded-lg p-8 mb-8">
          <div className="flex items-center gap-6 mb-8">
            <Avatar className="size-24">
              <AvatarImage src={user?.avatar} alt={user?.nome} />
              <AvatarFallback className="text-4xl font-bold">
                {user?.nome?.charAt(0).toUpperCase()}
              </AvatarFallback>
            </Avatar>
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-2">{user?.nome}</h2>
              <p className="text-muted-foreground">{user?.email}</p>
            </div>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-muted rounded-lg p-4 text-center">
              <div className="flex items-center justify-center gap-2 mb-2">
                <BookOpen className="w-5 h-5 text-accent" />
                <span className="text-2xl font-bold text-foreground">
                  {estatisticas?.totalResenhas || 0}
                </span>
              </div>
              <p className="text-sm text-muted-foreground">Resenhas</p>
            </div>
            <div className="bg-muted rounded-lg p-4 text-center">
              <div className="flex items-center justify-center gap-2 mb-2">
                <MessageSquare className="w-5 h-5 text-accent" />
                <span className="text-2xl font-bold text-foreground">
                  {estatisticas?.totalComentarios || 0}
                </span>
              </div>
              <p className="text-sm text-muted-foreground">Comentários</p>
            </div>
            <div className="bg-muted rounded-lg p-4 text-center">
              <div className="flex items-center justify-center gap-2 mb-2">
                <Award className="w-5 h-5 text-accent" />
                <span className="text-2xl font-bold text-foreground">
                  {estatisticas?.totalAvaliacoesRecebidas || 0}
                </span>
              </div>
              <p className="text-sm text-muted-foreground">Avaliações Recebidas</p>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="border-b border-border mb-8">
          <div className="flex gap-8">
            <button
              onClick={() => setAbaAtiva('resenhas')}
              className={`pb-4 px-2 font-semibold transition-colors ${
                abaAtiva === 'resenhas'
                  ? 'text-accent border-b-2 border-accent'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <div className="flex items-center gap-2">
                <BookOpen className="w-4 h-4" />
                Minhas Resenhas ({resenhas.length})
              </div>
            </button>
            <button
              onClick={() => setAbaAtiva('comentarios')}
              className={`pb-4 px-2 font-semibold transition-colors ${
                abaAtiva === 'comentarios'
                  ? 'text-accent border-b-2 border-accent'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <div className="flex items-center gap-2">
                <MessageSquare className="w-4 h-4" />
                Meus Comentários ({comentarios.length})
              </div>
            </button>
          </div>
        </div>

        {/* Content */}
        {abaAtiva === 'resenhas' ? (
          <div className="space-y-6">
            {resenhas.length === 0 ? (
              <div className="text-center py-12 bg-muted rounded-lg">
                <BookOpen className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <p className="text-muted-foreground mb-4">Você ainda não escreveu nenhuma resenha</p>
                <Button
                  onClick={() => setLocation('/')}
                  className="bg-accent text-accent-foreground hover:bg-accent/90"
                >
                  Voltar ao Feed
                </Button>
              </div>
            ) : (
              resenhas.map((resenha) => (
                <article
                  key={resenha.id}
                  className="bg-card border border-border rounded-lg p-6 hover:shadow-md transition-shadow"
                >
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-2xl font-bold text-foreground mb-1">
                        {resenha.titulo}
                      </h3>
                      <p className="text-muted-foreground">por {resenha.autor}</p>
                    </div>
                    <span className="text-sm text-muted-foreground">
                      {new Date(resenha.criadoEm).toLocaleDateString('pt-BR')}
                    </span>
                  </div>

                  <p className="text-foreground leading-relaxed mb-4 line-clamp-3">
                    {resenha.conteudo}
                  </p>

                  <div className="flex justify-between items-center pt-4 border-t border-border">
                    <span className="text-sm text-muted-foreground">
                      {resenha.avaliacoes.length} avaliações
                    </span>
                    <Button
                      onClick={() => setLocation('/')}
                      variant="ghost"
                      size="sm"
                      className="text-accent hover:text-accent/80"
                    >
                      Ver Resenha Completa
                    </Button>
                  </div>
                </article>
              ))
            )}
          </div>
        ) : (
          <div className="space-y-6">
            {comentarios.length === 0 ? (
              <div className="text-center py-12 bg-muted rounded-lg">
                <MessageSquare className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <p className="text-muted-foreground mb-4">Você ainda não escreveu nenhum comentário</p>
                <Button
                  onClick={() => setLocation('/')}
                  className="bg-accent text-accent-foreground hover:bg-accent/90"
                >
                  Voltar ao Feed
                </Button>
              </div>
            ) : (
              comentarios.map((comentario) => (
                <div
                  key={comentario.id}
                  className="bg-card border border-border rounded-lg p-6 hover:shadow-md transition-shadow"
                >
                  <div className="mb-4">
                    <p className="text-sm text-muted-foreground mb-2">
                      Comentário em:{' '}
                      <span className="font-semibold text-foreground">
                        {comentario.resenha.titulo}
                      </span>
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {new Date(comentario.criadoEm).toLocaleDateString('pt-BR')}
                    </p>
                  </div>

                  <p className="text-foreground leading-relaxed mb-4">
                    {comentario.conteudo}
                  </p>

                  <Button
                    onClick={() => setLocation('/')}
                    variant="ghost"
                    size="sm"
                    className="text-accent hover:text-accent/80"
                  >
                    Ver Resenha
                  </Button>
                </div>
              ))
            )}
          </div>
        )}
      </main>
    </div>
  );
}
