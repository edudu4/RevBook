import { useState } from 'react';
import { BookOpen } from 'lucide-react';

interface CapaLivroProps {
  src?: string;
  alt: string;
  className: string;
  loading?: 'lazy' | 'eager';
}

export default function CapaLivro({ src, alt, className, loading = 'lazy' }: CapaLivroProps) {
  const [falhou, setFalhou] = useState(false);

  if (!src || falhou) {
    return (
      <div className={`${className} bg-muted flex items-center justify-center`}>
        <BookOpen className="w-1/3 h-1/3 text-muted-foreground" />
      </div>
    );
  }

  return (
    <img
      src={src}
      alt={alt}
      loading={loading}
      decoding="async"
      onError={() => setFalhou(true)}
      className={className}
    />
  );
}
