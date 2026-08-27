import { useState } from 'react';
import { Star } from 'lucide-react';

interface AvaliacaoEstrelasProps {
  media: number;
  total: number;
  interativo?: boolean;
  onAvaliar?: (valor: number) => void;
}

export default function AvaliacaoEstrelas({ media, total, interativo, onAvaliar }: AvaliacaoEstrelasProps) {
  const [hover, setHover] = useState<number | null>(null);
  const exibido = hover ?? Math.round(media);

  return (
    <div className="flex items-center gap-2">
      <div className="flex" onMouseLeave={() => setHover(null)}>
        {[1, 2, 3, 4, 5].map((n) => (
          <button
            key={n}
            type="button"
            disabled={!interativo}
            onMouseEnter={() => interativo && setHover(n)}
            onClick={() => interativo && onAvaliar?.(n)}
            className={interativo ? 'cursor-pointer' : 'cursor-default'}
            title={interativo ? `Avaliar com ${n} estrela${n > 1 ? 's' : ''}` : undefined}
          >
            <Star
              className={`w-4 h-4 transition-colors ${
                n <= exibido ? 'fill-accent text-accent' : 'text-muted-foreground'
              }`}
            />
          </button>
        ))}
      </div>
      <span className="text-sm text-muted-foreground">
        {total > 0 ? media.toFixed(1) : '—'} ({total})
      </span>
    </div>
  );
}
