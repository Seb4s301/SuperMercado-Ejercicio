const API_URL = '/api/ventas';

export interface DetalleVenta {
  id?: number;
  nombreProd: string;
  cantProd: number;
  precio: number;
  subtotal?: number;
}

export interface Venta {
  id: number;
  fecha: string;
  estado: string;
  total: number;
  idSucursal: number;
  detalle: DetalleVenta[];
}

export interface VentaCreate {
  fecha: string;
  estado: string;
  idSucursal: number;
  detalle: DetalleVenta[];
}

function getAuthHeaders(token: string): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };
}

export async function getVentas(token: string): Promise<Venta[]> {
  const res = await fetch(API_URL, {
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al obtener ventas');
  }

  return res.json();
}

export async function createVenta(
  token: string,
  data: VentaCreate
): Promise<Venta> {
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
    throw new Error('Error al crear venta');
  }

  return res.json();
}

export async function deleteVenta(
  token: string,
  id: number
): Promise<void> {
  const res = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders(token),
  });

  if (!res.ok) {
    throw new Error('Error al eliminar venta');
  }
}
