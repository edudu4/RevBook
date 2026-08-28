import { API_BASE_URL } from '@/lib/api';

let renovacaoEmAndamento: Promise<boolean> | null = null;

function renovarSessao(): Promise<boolean> {
  if (!renovacaoEmAndamento) {
    renovacaoEmAndamento = fetch(`${API_BASE_URL}/auth/refresh`, { method: 'POST', credentials: 'include' })
      .then((response) => response.ok)
      .catch(() => false)
      .finally(() => {
        renovacaoEmAndamento = null;
      });
  }
  return renovacaoEmAndamento;
}

export async function fetchAutenticado(input: string, init?: RequestInit): Promise<Response> {
  const response = await fetch(input, { ...init, credentials: 'include' });

  if (response.status !== 401) {
    return response;
  }

  const renovou = await renovarSessao();
  if (!renovou) {
    if (window.location.pathname !== '/login') {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return response;
  }

  return fetch(input, { ...init, credentials: 'include' });
}
