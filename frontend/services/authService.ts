const API_URL = '/api/auth';

export interface LoginResponse {
  token: string;
  rol: string;
  username: string;
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const res = await fetch(`${API_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });

  if (res.status === 401) {
    throw new Error('Credenciales incorrectas');
  }

  if (res.status === 429) {
    throw new Error('Demasiados intentos. Intenta más tarde');
  }

  if (!res.ok) {
    throw new Error('Error al iniciar sesión');
  }

  return res.json();
}
