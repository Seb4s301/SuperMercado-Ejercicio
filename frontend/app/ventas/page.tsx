'use client';

import { useState, useEffect, FormEvent } from 'react';
import MainLayout from '../../components/MainLayout';
import ProtectedRoute from '../../components/ProtectedRoute';
import { useAuth } from '../../context/AuthContext';
import { Venta, VentaCreate, DetalleVenta, getVentas, createVenta, deleteVenta } from '../../services/ventaService';
import { Sucursal, getSucursales } from '../../services/sucursalService';
import { Producto, getProductos } from '../../services/productoService';
import styles from './ventas.module.css';

interface DetalleForm {
  nombreProd: string;
  cantProd: string;
  precio: string;
}

const initialDetalle: DetalleForm[] = [{ nombreProd: '', cantProd: '1', precio: '' }];

const initialForm = {
  fecha: '',
  estado: 'pendiente',
  idSucursal: '',
};

export default function VentasPage() {
  const { token, isAdmin } = useAuth();

  const [ventas, setVentas] = useState<Venta[]>([]);
  const [sucursales, setSucursales] = useState<Sucursal[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(initialForm);
  const [detalles, setDetalles] = useState<DetalleForm[]>(initialDetalle);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [expandedId, setExpandedId] = useState<number | null>(null);

  useEffect(() => {
    if (token) {
      loadData();
    }
  }, [token]);

  async function loadData() {
    try {
      setLoading(true);
      const [ventasData, sucursalesData, productosData] = await Promise.all([
        getVentas(token!),
        getSucursales(token!),
        getProductos(token!),
      ]);
      setVentas(ventasData);
      setSucursales(sucursalesData);
      setProductos(productosData);
    } catch {
      setMessage({ type: 'error', text: 'Error al cargar datos' });
    } finally {
      setLoading(false);
    }
  }

  function openCreate() {
    setForm(initialForm);
    setDetalles(initialDetalle);
    setFieldErrors({});
    setShowForm(true);
  }

  function closeForm() {
    setShowForm(false);
    setForm(initialForm);
    setDetalles(initialDetalle);
    setFieldErrors({});
  }

  function getProductoPrecio(nombre: string): number {
    const prod = productos.find((p) => p.nombre === nombre);
    return prod ? prod.precio : 0;
  }

  function handleDetalleChange(index: number, field: keyof DetalleForm, value: string) {
    setDetalles((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [field]: value };
      if (field === 'nombreProd') {
        const precio = getProductoPrecio(value);
        next[index].precio = precio > 0 ? String(precio) : '';
      }
      return next;
    });
  }

  function addDetalle() {
    setDetalles((prev) => [...prev, { nombreProd: '', cantProd: '1', precio: '' }]);
  }

  function removeDetalle(index: number) {
    if (detalles.length <= 1) return;
    setDetalles((prev) => prev.filter((_, i) => i !== index));
  }

  function calcSubtotal(d: DetalleForm): number {
    const cant = parseFloat(d.cantProd) || 0;
    const precio = parseFloat(d.precio) || 0;
    return cant * precio;
  }

  function calcTotal(): number {
    return detalles.reduce((sum, d) => sum + calcSubtotal(d), 0);
  }

  function getNombreSucursal(id: number): string {
    const s = sucursales.find((s) => s.id === id);
    return s ? s.nombre : `Sucursal #${id}`;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setFieldErrors({});
    setMessage(null);

    const errores: Record<string, string> = {};
    if (!form.fecha) errores.fecha = 'La fecha es requerida';
    if (!form.idSucursal) errores.idSucursal = 'La sucursal es requerida';
    const validDetalles = detalles.filter((d) => d.nombreProd && d.cantProd);
    if (validDetalles.length === 0) errores.detalle = 'Debe agregar al menos un producto';

    if (Object.keys(errores).length > 0) {
      setFieldErrors(errores);
      return;
    }

    const body: VentaCreate = {
      fecha: form.fecha,
      estado: form.estado,
      idSucursal: parseInt(form.idSucursal, 10),
      detalle: validDetalles.map((d) => ({
        nombreProd: d.nombreProd,
        cantProd: parseInt(d.cantProd, 10),
        precio: parseFloat(d.precio),
      })),
    };

    try {
      await createVenta(token!, body);
      setMessage({ type: 'success', text: 'Venta creada correctamente' });
      closeForm();
      await loadData();
    } catch (err: any) {
      if (err?.type === 'validation') {
        setFieldErrors(err.errors);
      } else {
        setMessage({ type: 'error', text: err.message || 'Error al crear venta' });
      }
    }
  }

  async function confirmDelete() {
    if (!deleteId) return;
    try {
      await deleteVenta(token!, deleteId);
      setMessage({ type: 'success', text: 'Venta eliminada correctamente' });
      setDeleteId(null);
      await loadData();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.message || 'Error al eliminar venta' });
      setDeleteId(null);
    }
  }

  function formatCurrency(value: number): string {
    return '$' + value.toLocaleString('es-CL');
  }

  function getEstadoClass(estado: string): string {
    switch (estado.toLowerCase()) {
      case 'pagada': return styles.estadoPagada;
      case 'cancelada': return styles.estadoCancelada;
      default: return styles.estadoPendiente;
    }
  }

  const ventasFiltradas = ventas.filter((v) => {
    const nombre = getNombreSucursal(v.idSucursal).toLowerCase();
    return nombre.includes(busqueda.toLowerCase()) || v.estado.toLowerCase().includes(busqueda.toLowerCase());
  });

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="page-header">
          <h1>Ventas</h1>
          <p>Gestión de ventas del supermercado</p>
        </div>

        {message && (
          <div
            className={`${styles.message} ${
              message.type === 'success' ? styles.messageSuccess : styles.messageError
            }`}
          >
            {message.text}
          </div>
        )}

        <div className={styles.toolbar}>
          <input
            type="text"
            placeholder="Buscar por sucursal o estado..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className={styles.searchInput}
          />
          {isAdmin && (
            <button className={styles.btnNew} onClick={openCreate}>
              Nueva Venta
            </button>
          )}
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-secondary)', fontSize: 14 }}>Cargando ventas...</p>
        ) : ventasFiltradas.length === 0 ? (
          <div className={styles.emptyState}>No se encontraron ventas</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Sucursal</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Detalle</th>
                {isAdmin && <th>Acciones</th>}
              </tr>
            </thead>
            <tbody>
              {ventasFiltradas.map((v) => (
                <>
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td>{v.fecha}</td>
                    <td>{getNombreSucursal(v.idSucursal)}</td>
                    <td>{formatCurrency(v.total)}</td>
                    <td>
                      <span className={`${styles.estado} ${getEstadoClass(v.estado)}`}>
                        {v.estado}
                      </span>
                    </td>
                    <td>
                      <button
                        className={styles.expandBtn}
                        onClick={() => setExpandedId(expandedId === v.id ? null : v.id)}
                      >
                        {expandedId === v.id ? 'Ocultar' : 'Ver'}
                      </button>
                    </td>
                    {isAdmin && (
                      <td>
                        <div className={styles.actions}>
                          <button
                            className={styles.btnDelete}
                            onClick={() => setDeleteId(v.id)}
                          >
                            Eliminar
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                  {expandedId === v.id && (
                    <tr className={styles.expandedRow} key={`expand-${v.id}`}>
                      <td colSpan={isAdmin ? 7 : 6}>
                        <table className={styles.detalleTable}>
                          <thead>
                            <tr>
                              <th>Producto</th>
                              <th>Cantidad</th>
                              <th>Precio</th>
                              <th>Subtotal</th>
                            </tr>
                          </thead>
                          <tbody>
                            {v.detalle.map((d, i) => (
                              <tr key={i}>
                                <td>{d.nombreProd}</td>
                                <td>{d.cantProd}</td>
                                <td>{formatCurrency(d.precio)}</td>
                                <td>{formatCurrency(d.subtotal || d.cantProd * d.precio)}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </td>
                    </tr>
                  )}
                </>
              ))}
            </tbody>
          </table>
        )}

        {showForm && (
          <div className={styles.overlay} onClick={closeForm}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
              <h2>Nueva Venta</h2>
              <form onSubmit={handleSubmit}>
                <div className={styles.formRow}>
                  <div className={styles.formGroup}>
                    <label>Fecha</label>
                    <input
                      type="date"
                      value={form.fecha}
                      onChange={(e) => setForm((prev) => ({ ...prev, fecha: e.target.value }))}
                    />
                    {fieldErrors.fecha && (
                      <div className={styles.fieldError}>{fieldErrors.fecha}</div>
                    )}
                  </div>
                  <div className={styles.formGroup}>
                    <label>Estado</label>
                    <select
                      value={form.estado}
                      onChange={(e) => setForm((prev) => ({ ...prev, estado: e.target.value }))}
                    >
                      <option value="pendiente">Pendiente</option>
                      <option value="pagada">Pagada</option>
                      <option value="cancelada">Cancelada</option>
                    </select>
                  </div>
                </div>

                <div className={styles.formGroup}>
                  <label>Sucursal</label>
                  <select
                    value={form.idSucursal}
                    onChange={(e) => setForm((prev) => ({ ...prev, idSucursal: e.target.value }))}
                  >
                    <option value="">Seleccionar sucursal</option>
                    {sucursales.map((s) => (
                      <option key={s.id} value={s.id}>{s.nombre}</option>
                    ))}
                  </select>
                  {fieldErrors.idSucursal && (
                    <div className={styles.fieldError}>{fieldErrors.idSucursal}</div>
                  )}
                </div>

                <div className={styles.detalleSection}>
                  <div className={styles.detalleHeader}>
                    <h3>Productos</h3>
                    <button type="button" className={styles.btnAddProduct} onClick={addDetalle}>
                      + Agregar producto
                    </button>
                  </div>
                  {fieldErrors.detalle && (
                    <div className={styles.fieldError}>{fieldErrors.detalle}</div>
                  )}
                  {detalles.map((d, i) => (
                    <div className={styles.detalleRow} key={i}>
                      <div>
                        <label>Producto</label>
                        <select
                          value={d.nombreProd}
                          onChange={(e) => handleDetalleChange(i, 'nombreProd', e.target.value)}
                        >
                          <option value="">Seleccionar</option>
                          {productos.map((p) => (
                            <option key={p.id} value={p.nombre}>{p.nombre}</option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label>Cantidad</label>
                        <input
                          type="number"
                          min="1"
                          value={d.cantProd}
                          onChange={(e) => handleDetalleChange(i, 'cantProd', e.target.value)}
                        />
                      </div>
                      <div>
                        <label>Precio</label>
                        <input
                          type="number"
                          step="0.01"
                          min="0"
                          value={d.precio}
                          readOnly
                        />
                      </div>
                      <div>
                        <label>Subtotal</label>
                        <input
                          type="text"
                          value={formatCurrency(calcSubtotal(d))}
                          readOnly
                        />
                      </div>
                      <div>
                        <label>&nbsp;</label>
                        <button
                          type="button"
                          className={styles.btnRemoveRow}
                          onClick={() => removeDetalle(i)}
                          disabled={detalles.length <= 1}
                        >
                          ×
                        </button>
                      </div>
                    </div>
                  ))}
                  <div className={styles.totalRow}>
                    <span className={styles.totalLabel}>Total:</span>
                    <span className={styles.totalValue}>{formatCurrency(calcTotal())}</span>
                  </div>
                </div>

                <div className={styles.formActions}>
                  <button type="button" className={styles.btnCancel} onClick={closeForm}>
                    Cancelar
                  </button>
                  <button type="submit" className={styles.btnSave}>
                    Crear Venta
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {deleteId !== null && (
          <div className={styles.overlay} onClick={() => setDeleteId(null)}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
              <h2>Confirmar Eliminación</h2>
              <p className={styles.confirmText}>
                ¿Estás seguro de que deseas eliminar esta venta? Esta acción no se puede
                deshacer.
              </p>
              <div className={styles.confirmActions}>
                <button
                  className={styles.btnCancel}
                  onClick={() => setDeleteId(null)}
                >
                  Cancelar
                </button>
                <button className={styles.btnConfirmDelete} onClick={confirmDelete}>
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        )}
      </MainLayout>
    </ProtectedRoute>
  );
}
