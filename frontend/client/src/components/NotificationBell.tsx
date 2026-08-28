import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'wouter';
import { Bell, MessageSquare, Star } from 'lucide-react';
import { API_BASE_URL, type NotificationApi } from '@/lib/api';
import { fetchAutenticado } from '@/lib/fetchAutenticado';
import { paraNotificacao } from '@/lib/mapeadores';
import type { Notificacao } from '@/types/dominio';

const INTERVALO_POLLING_MS = 30000;

function tempoRelativo(iso: string): string {
  const diffMs = Date.now() - new Date(iso).getTime();
  const minutos = Math.floor(diffMs / 60000);
  if (minutos < 1) return 'agora';
  if (minutos < 60) return `há ${minutos} min`;
  const horas = Math.floor(minutos / 60);
  if (horas < 24) return `há ${horas}h`;
  const dias = Math.floor(horas / 24);
  return `há ${dias}d`;
}

export default function NotificationBell() {
  const [, setLocation] = useLocation();
  const [aberto, setAberto] = useState(false);
  const [naoLidas, setNaoLidas] = useState(0);
  const [notificacoes, setNotificacoes] = useState<Notificacao[]>([]);
  const [carregando, setCarregando] = useState(false);
  const painelRef = useRef<HTMLDivElement>(null);

  const buscarContagem = async () => {
    try {
      const response = await fetchAutenticado(`${API_BASE_URL}/notifications/unread-count`);
      if (response.ok) {
        const data: { count: number } = await response.json();
        setNaoLidas(data.count);
      }
    } catch (error) {
      console.error('Failed to fetch unread notifications count:', error);
    }
  };

  useEffect(() => {
    buscarContagem();
    const intervalo = setInterval(buscarContagem, INTERVALO_POLLING_MS);
    return () => clearInterval(intervalo);
  }, []);

  useEffect(() => {
    const handleClickFora = (event: MouseEvent) => {
      if (painelRef.current && !painelRef.current.contains(event.target as Node)) {
        setAberto(false);
      }
    };
    if (aberto) {
      document.addEventListener('mousedown', handleClickFora);
    }
    return () => document.removeEventListener('mousedown', handleClickFora);
  }, [aberto]);

  const abrirPainel = async () => {
    const novoEstado = !aberto;
    setAberto(novoEstado);
    if (novoEstado) {
      setCarregando(true);
      try {
        const response = await fetchAutenticado(`${API_BASE_URL}/notifications`);
        if (response.ok) {
          const data: NotificationApi[] = await response.json();
          setNotificacoes(data.map(paraNotificacao));
        }
      } catch (error) {
        console.error('Failed to fetch notifications:', error);
      } finally {
        setCarregando(false);
      }
    }
  };

  const irParaResenha = async (notificacao: Notificacao) => {
    setAberto(false);
    setLocation(`/reviews/${notificacao.resenhaId}`);

    if (!notificacao.lida) {
      try {
        await fetchAutenticado(`${API_BASE_URL}/notifications/${notificacao.id}/read`, {
          method: 'POST',
        });
        setNaoLidas((atual) => Math.max(0, atual - 1));
      } catch (error) {
        console.error('Failed to mark notification as read:', error);
      }
    }
  };

  const marcarTodasComoLidas = async () => {
    try {
      await fetchAutenticado(`${API_BASE_URL}/notifications/read-all`, { method: 'POST' });
      setNotificacoes((atual) => atual.map((n) => ({ ...n, lida: true })));
      setNaoLidas(0);
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error);
    }
  };

  return (
    <div className="relative" ref={painelRef}>
      <button
        onClick={abrirPainel}
        className="relative p-2 rounded-full hover:bg-accent/10 transition-colors"
        title="Notificações"
      >
        <Bell className="w-5 h-5 text-foreground" />
        {naoLidas > 0 && (
          <span
            className="absolute -top-0.5 -right-0.5 flex items-center justify-center min-w-[18px] h-[18px] px-1 rounded-full text-[10px] font-bold"
            style={{ backgroundColor: '#7A3038', color: '#FFFCF7' }}
          >
            {naoLidas > 9 ? '9+' : naoLidas}
          </span>
        )}
      </button>

      {aberto && (
        <div className="fixed left-4 right-4 top-16 sm:absolute sm:left-auto sm:right-0 sm:top-auto sm:mt-2 sm:w-80 bg-card border border-border rounded-lg shadow-lg z-50 overflow-hidden">
          <div className="flex items-center justify-between px-4 py-3 border-b border-border">
            <p className="font-semibold text-foreground">Notificações</p>
            {notificacoes.some((n) => !n.lida) && (
              <button onClick={marcarTodasComoLidas} className="text-xs text-accent hover:underline">
                Marcar todas como lidas
              </button>
            )}
          </div>

          <div className="max-h-80 overflow-y-auto">
            {carregando && <p className="p-4 text-sm text-muted-foreground text-center">Carregando...</p>}

            {!carregando && notificacoes.length === 0 && (
              <p className="p-4 text-sm text-muted-foreground text-center">Nenhuma notificação ainda.</p>
            )}

            {!carregando &&
              notificacoes.map((notificacao) => (
                <button
                  key={notificacao.id}
                  onClick={() => irParaResenha(notificacao)}
                  className={`w-full text-left px-4 py-3 border-b border-border last:border-0 hover:bg-accent/10 transition-colors flex gap-3 items-start ${
                    notificacao.lida ? '' : 'bg-accent/5'
                  }`}
                >
                  <span className="mt-0.5 text-accent flex-shrink-0">
                    {notificacao.tipo === 'COMENTARIO' ? (
                      <MessageSquare className="w-4 h-4" />
                    ) : (
                      <Star className="w-4 h-4 fill-star text-star" />
                    )}
                  </span>
                  <span className="min-w-0">
                    <p className="text-sm text-foreground">
                      <span className="font-semibold">{notificacao.nomeAtor}</span>{' '}
                      {notificacao.tipo === 'COMENTARIO' ? 'comentou em' : 'avaliou'}{' '}
                      <span className="font-semibold">{notificacao.tituloLivro}</span>
                    </p>
                    <p className="text-xs text-muted-foreground mt-0.5">{tempoRelativo(notificacao.criadoEm)}</p>
                  </span>
                  {!notificacao.lida && (
                    <span className="w-2 h-2 rounded-full bg-accent flex-shrink-0 mt-1.5" aria-hidden="true" />
                  )}
                </button>
              ))}
          </div>
        </div>
      )}
    </div>
  );
}
