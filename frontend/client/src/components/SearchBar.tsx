import { useState, useEffect } from 'react';
import { Search, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { API_BASE_URL } from '@/lib/api';
import type { FiltrosBusca } from '@/types/dominio';

interface SearchBarProps {
  onSearch: (filtros: FiltrosBusca) => void;
  onClear: () => void;
}

export default function SearchBar({ onSearch, onClear }: SearchBarProps) {
  const [titulo, setTitulo] = useState('');
  const [autor, setAutor] = useState('');
  const [genero, setGenero] = useState('');
  const [generos, setGeneros] = useState<string[]>([]);
  const [isOpen, setIsOpen] = useState(false);

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

  const handleBuscar = () => {
    onSearch({
      titulo: titulo || undefined,
      autor: autor || undefined,
      genero: genero || undefined,
    });
  };

  const handleLimpar = () => {
    setTitulo('');
    setAutor('');
    setGenero('');
    onClear();
  };

  const temFiltros = titulo || autor || genero;

  return (
    <div className="bg-card border border-border rounded-lg p-6 mb-8">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-foreground flex items-center gap-2">
          <Search className="w-5 h-5 text-accent" />
          Busca Avançada
        </h3>
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="text-muted-foreground hover:text-foreground transition-colors"
        >
          {isOpen ? '▼' : '▶'}
        </button>
      </div>

      {isOpen && (
        <div className="space-y-4">
          {/* Title Search */}
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              Título do Livro
            </label>
            <input
              type="text"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              placeholder="Digite o título..."
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent"
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  handleBuscar();
                }
              }}
            />
          </div>

          {/* Author Search */}
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              Autor
            </label>
            <input
              type="text"
              value={autor}
              onChange={(e) => setAutor(e.target.value)}
              placeholder="Digite o nome do autor..."
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent"
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  handleBuscar();
                }
              }}
            />
          </div>

          {/* Genre Select */}
          <div>
            <label className="block text-sm font-medium text-foreground mb-2">
              Gênero
            </label>
            <select
              value={genero}
              onChange={(e) => setGenero(e.target.value)}
              className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent"
            >
              <option value="">Selecione um gênero...</option>
              {generos.map((g) => (
                <option key={g} value={g}>
                  {g}
                </option>
              ))}
            </select>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <Button
              onClick={handleBuscar}
              className="flex-1 bg-accent text-accent-foreground hover:bg-accent/90"
            >
              <Search className="w-4 h-4 mr-2" />
              Buscar
            </Button>
            {temFiltros && (
              <Button
                onClick={handleLimpar}
                variant="outline"
                className="flex-1"
              >
                <X className="w-4 h-4 mr-2" />
                Limpar
              </Button>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
