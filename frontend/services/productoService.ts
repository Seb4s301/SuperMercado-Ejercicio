const API_URL = '/api/productos';

export interface Producto {
  id: number;
  nombre: string;
  categoria: string;
  precio: number;
  cantidad: number;
}

function getAuthHeaders(token: string): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}

export async function getProductos(token: string): Promise<Producto[]> {
  const res = await fetch(API_URL, {
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al obtener productos');
  }

  return res.json();
}

export async function createProducto(
  token: string,
  data: Omit<Producto, 'id'>
): Promise<Producto> {
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
    throw new Error('Error al crear producto');
  }

  return res.json();
}

export async function updateProducto(
  token: string,
  id: number,
  data: Omit<Producto, 'id'>
): Promise<Producto> {
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
    throw new Error('Error al actualizar producto');
  }

  return res.json();
}

export async function deleteProducto(
  token: string,
  id: number
): Promise<void> {
  const res = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al eliminar producto');
  }
}
