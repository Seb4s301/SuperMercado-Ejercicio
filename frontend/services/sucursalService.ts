const API_URL = '/api/sucursales';

export interface Sucursal {
  id: number;
  nombre: string;
  direccion: string;
}

function getAuthHeaders(token: string): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}

export async function getSucursales(token: string): Promise<Sucursal[]> {
  const res = await fetch(API_URL, {
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al obtener sucursales');
  }

  return res.json();
}

export async function createSucursal(
  token: string,
  data: Omit<Sucursal, 'id'>
): Promise<Sucursal> {
  const res = await fetch(API_URL, {
    method: 'POST',
    headers: getAuthHeaders(token),
    body: JSON.stringify(data),
  });

  if (res.status === 400) {
    const errors = await res.json();
    throw { type: 'validation', errors };
  }

  if (!res.ok) {
    throw new Error('Error al crear sucursal');
  }

  return res.json();
}

export async function updateSucursal(
  token: string,
  id: number,
  data: Omit<Sucursal, 'id'>
): Promise<Sucursal> {
  const res = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: getAuthHeaders(token),
    body: JSON.stringify(data),
  });

  if (res.status === 400) {
    const errors = await res.json();
    throw { type: 'validation', errors };
  }

  if (!res.ok) {
    throw new Error('Error al actualizar sucursal');
  }

  return res.json();
}

export async function deleteSucursal(
  token: string,
  id: number
): Promise<void> {
  const res = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al eliminar sucursal');
  }
}
