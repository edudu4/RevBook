import { useEffect, useRef, useState } from 'react';
import { Search, X } from 'lucide-react';
import { API_BASE_URL } from '@/lib/api';
import type { FiltrosBusca } from '@/types/dominio';

interface SearchBarProps {
  onSearch: (filtros: FiltrosBusca) => void;
  onClear: () => void;
  buscando?: boolean;
}

const DEBOUNCE_MS = 350;

export default function SearchBar({ onSearch, onClear, buscando }: SearchBarProps) {
  const [termo, setTermo] = useState('');
  const [genero, setGenero] = useState<string | null>(null);
  const [generos, setGeneros] = useState<string[]>([]);
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    buscarGeneros();
  }, []);

  const buscarGeneros = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/genres`);
      if (response.ok) {
        const data = await response.json();
        setGeneros(data);
      }
    } catch (error) {
      console.error('Failed to fetch genres:', error);
    }
  };

  const disparar = (novoTermo: string, novoGenero: string | null) => {
    if (!novoTermo.trim() && !novoGenero) {
      onClear();
      return;
    }
    onSearch({ termo: novoTermo.trim() || undefined, genero: novoGenero || undefined });
  };

  const handleTermoChange = (valor: string) => {
    setTermo(valor);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => disparar(valor, genero), DEBOUNCE_MS);
  };

  const handleToggleGenero = (g: string) => {
    const novoGenero = genero === g ? null : g;
    setGenero(novoGenero);
    disparar(termo, novoGenero);
  };

  const handleLimpar = () => {
    setTermo('');
    setGenero(null);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    onClear();
  };

  const temFiltros = termo.trim() || genero;

  return (
    <div className="mb-8">
      <div className="relative">
        {buscando ? (
          <div className="spinner-revbook spinner-revbook--pequeno absolute left-4 top-1/2 -translate-y-1/2" />
        ) : (
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
        )}
        <input
          type="text"
          value={termo}
          onChange={(e) => handleTermoChange(e.target.value)}
          placeholder="Buscar por título ou autor..."
          className="w-full pl-12 pr-11 py-3 border border-border rounded-full bg-card text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent transition-shadow"
        />
        {temFiltros && (
          <button
            onClick={handleLimpar}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
            title="Limpar busca"
          >
            <X className="w-5 h-5" />
          </button>
        )}
      </div>

      {generos.length > 0 && (
        <div className="flex flex-wrap gap-2 mt-3">
          {generos.map((g) => (
            <button
              key={g}
              onClick={() => handleToggleGenero(g)}
              className={`text-sm px-3 py-1.5 rounded-full border transition-colors ${
                genero === g
                  ? 'bg-accent text-accent-foreground border-accent'
                  : 'bg-card text-muted-foreground border-border hover:border-accent hover:text-accent'
              }`}
            >
              {g}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
