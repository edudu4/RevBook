interface SpinnerProps {
  label?: string;
  telaCheia?: boolean;
  tamanho?: 'normal' | 'pequeno';
}

export default function Spinner({ label = 'Carregando...', telaCheia, tamanho = 'normal' }: SpinnerProps) {
  const pequeno = tamanho === 'pequeno';

  const conteudo = (
    <div className={`flex items-center justify-center ${pequeno ? 'gap-2' : 'flex-col gap-3'}`}>
      <div className={`spinner-revbook ${pequeno ? 'spinner-revbook--pequeno' : ''}`} role="status" aria-label={label} />
      <p className="text-sm text-muted-foreground">{label}</p>
    </div>
  );

  if (telaCheia) {
    return <div className="min-h-screen bg-background flex items-center justify-center">{conteudo}</div>;
  }

  return <div className={`${pequeno ? 'py-2' : 'py-16'} flex items-center justify-center`}>{conteudo}</div>;
}
