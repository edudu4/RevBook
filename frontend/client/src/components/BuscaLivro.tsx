import { useEffect, useRef, useState } from 'react';
import { X } from 'lucide-react';
import { API_BASE_URL, type BookSearchResultApi } from '@/lib/api';
import { paraLivroEncontrado } from '@/lib/mapeadores';
import type { LivroEncontrado } from '@/types/dominio';

interface BuscaLivroProps {
  livroSelecionado: LivroEncontrado | null;
  onSelecionar: (livro: LivroEncontrado) => void;
  onLimpar: () => void;
}

export default function BuscaLivro({ livroSelecionado, onSelecionar, onLimpar }: BuscaLivroProps) {
  const [termo, setTermo] = useState('');
  const [resultados, setResultados] = useState<LivroEncontrado[]>([]);
  const [buscando, setBuscando] = useState(false);
  const [mostrarResultados, setMostrarResultados] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    const handleClickFora = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setMostrarResultados(false);
      }
    };
    document.addEventListener('mousedown', handleClickFora);
    return () => document.removeEventListener('mousedown', handleClickFora);
  }, []);

  useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);

    if (termo.trim().length < 3) {
      setResultados([]);
      return;
    }

    debounceRef.current = setTimeout(async () => {
      try {
        setBuscando(true);
        const response = await fetch(`${API_BASE_URL}/books/search?q=${encodeURIComponent(termo)}`);
        if (response.ok) {
          const data: BookSearchResultApi[] = await response.json();
          setResultados(data.map(paraLivroEncontrado));
          setMostrarResultados(true);
        }
      } catch (error) {
        console.error('Failed to search books:', error);
      } finally {
        setBuscando(false);
      }
    }, 400);

    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [termo]);

  if (livroSelecionado) {
    return (
      <div className="flex items-center gap-3 p-3 border border-border rounded-lg bg-background">
        {livroSelecionado.capaUrl ? (
          <img
            src={livroSelecionado.capaUrl}
            alt={livroSelecionado.titulo}
            decoding="async"
            className="w-12 h-16 object-cover rounded flex-shrink-0"
          />
        ) : (
          <div className="w-12 h-16 bg-muted rounded flex-shrink-0" />
        )}
        <div className="flex-1 min-w-0">
          <p className="font-medium text-foreground truncate">{livroSelecionado.titulo}</p>
          <p className="text-sm text-muted-foreground truncate">{livroSelecionado.autor}</p>
          {livroSelecionado.genero && (
            <span className="inline-block mt-1 text-xs bg-accent/10 text-accent px-2 py-0.5 rounded">
              {livroSelecionado.genero}
            </span>
          )}
        </div>
        <button
          type="button"
          onClick={() => {
            onLimpar();
            setTermo('');
          }}
          className="text-muted-foreground hover:text-foreground p-1 flex-shrink-0"
          title="Trocar livro"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    );
  }

  return (
    <div className="relative" ref={containerRef}>
      <input
        type="text"
        value={termo}
        onChange={(e) => setTermo(e.target.value)}
        onFocus={() => resultados.length > 0 && setMostrarResultados(true)}
        placeholder="Digite o título do livro..."
        className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent"
      />

      {mostrarResultados && buscando && (
        <div className="absolute z-10 mt-1 w-full bg-card border border-border rounded-lg shadow-lg p-3">
          <p className="text-sm text-muted-foreground">Buscando...</p>
        </div>
      )}

      {mostrarResultados && !buscando && resultados.length > 0 && (
        <div className="absolute z-10 mt-1 w-full bg-card border border-border rounded-lg shadow-lg max-h-72 overflow-y-auto">
          {resultados.map((livro) => (
            <button
              key={livro.googleBooksId}
              type="button"
              onClick={() => {
                onSelecionar(livro);
                setMostrarResultados(false);
              }}
              className="flex items-center gap-3 w-full p-3 text-left hover:bg-muted transition-colors border-b border-border last:border-b-0"
            >
              {livro.capaUrl ? (
                <img
                  src={livro.capaUrl}
                  alt={livro.titulo}
                  loading="lazy"
                  decoding="async"
                  className="w-10 h-14 object-cover rounded flex-shrink-0"
                />
              ) : (
                <div className="w-10 h-14 bg-muted rounded flex-shrink-0" />
              )}
              <div className="min-w-0">
                <p className="font-medium text-foreground truncate">{livro.titulo}</p>
                <p className="text-sm text-muted-foreground truncate">{livro.autor}</p>
              </div>
            </button>
          ))}
        </div>
      )}

      {mostrarResultados && !buscando && termo.trim().length >= 3 && resultados.length === 0 && (
        <div className="absolute z-10 mt-1 w-full bg-card border border-border rounded-lg shadow-lg p-3">
          <p className="text-sm text-muted-foreground">Nenhum livro encontrado.</p>
        </div>
      )}
    </div>
  );
}
