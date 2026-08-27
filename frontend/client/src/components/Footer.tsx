import { Linkedin } from 'lucide-react';

export default function Footer() {
  const ano = new Date().getFullYear();

  return (
    <footer className="mt-auto border-t border-border bg-card">
      <div className="max-w-7xl mx-auto px-4 py-8 flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <span
            className="flex items-center justify-center w-7 h-7 rounded-md font-bold text-sm flex-shrink-0"
            style={{ backgroundColor: '#542229', color: '#D5A62A' }}
            aria-hidden="true"
          >
            R
          </span>
          <div>
            <p className="font-bold text-foreground leading-tight">RevBook</p>
            <p className="text-xs text-muted-foreground">Resenhas de livros, por quem lê de verdade.</p>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <a
            href="https://www.linkedin.com/in/edudu4"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-accent transition-colors"
          >
            <Linkedin className="w-4 h-4" />
            Contato
          </a>
          <p className="text-xs text-muted-foreground">© {ano} RevBook</p>
        </div>
      </div>
    </footer>
  );
}
