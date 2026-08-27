export async function fetchAutenticado(input: string, init?: RequestInit): Promise<Response> {
  const response = await fetch(input, { ...init, credentials: 'include' });

  if (response.status === 401 && window.location.pathname !== '/login') {
    localStorage.removeItem('user');
    window.location.href = '/login';
  }

  return response;
}
