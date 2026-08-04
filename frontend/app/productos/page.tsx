'use client';

import { useState, useEffect, FormEvent } from 'react';
import MainLayout from '../../components/MainLayout';
import ProtectedRoute from '../../components/ProtectedRoute';
import { useAuth } from '../../context/AuthContext';
import {
  Producto,
  getProductos,
  createProducto,
  updateProducto,
  deleteProducto,
} from '../../services/productoService';
import styles from './productos.module.css';

const initialForm = { nombre: '', categoria: '', precio: '', cantidad: '' };

export default function ProductosPage() {
  const { token, isAdmin } = useAuth();

  const [productos, setProductos] = useState<Producto[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState(initialForm);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    if (token) loadProductos();
  }, [token]);

  async function loadProductos() {
    try {
      setLoading(true);
      const data = await getProductos(token!);
      setProductos(data);
    } catch {
      setMessage({ type: 'error', text: 'Error al cargar productos' });
    } finally {
      setLoading(false);
    }
  }

  function openCreate() {
    setEditingId(null);
    setForm(initialForm);
    setFieldErrors({});
    setShowForm(true);
  }

  function openEdit(p: Producto) {
    setEditingId(p.id);
    setForm({
      nombre: p.nombre,
      categoria: p.categoria,
      precio: String(p.precio),
      cantidad: String(p.cantidad),
    });
    setFieldErrors({});
    setShowForm(true);
  }

  function closeForm() {
    setShowForm(false);
    setEditingId(null);
    setForm(initialForm);
    setFieldErrors({});
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setFieldErrors({});
    setMessage(null);

    const body = {
      nombre: form.nombre,
      categoria: form.categoria,
      precio: parseFloat(form.precio),
      cantidad: parseInt(form.cantidad, 10),
    };

    try {
      if (editingId) {
        await updateProducto(token!, editingId, body);
        setMessage({ type: 'success', text: 'Producto actualizado correctamente' });
      } else {
        await createProducto(token!, body);
        setMessage({ type: 'success', text: 'Producto creado correctamente' });
      }
      closeForm();
      await loadProductos();
    } catch (err: any) {
      if (err?.type === 'validation') {
        setFieldErrors(err.errors);
      } else {
        setMessage({ type: 'error', text: err.message || 'Error al guardar producto' });
      }
    }
  }

  async function confirmDelete() {
    if (!deleteId) return;
    try {
      await deleteProducto(token!, deleteId);
      setMessage({ type: 'success', text: 'Producto eliminado correctamente' });
      setDeleteId(null);
      await loadProductos();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.message || 'Error al eliminar producto' });
      setDeleteId(null);
    }
  }

  function handleInputChange(field: string, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
    if (fieldErrors[field]) {
      setFieldErrors((prev) => {
        const next = { ...prev };
        delete next[field];
        return next;
      });
    }
  }

  const productosFiltrados = productos.filter((p) =>
    p.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="page-header">
          <h1>Productos</h1>
          <p>Gestión de productos del supermercado</p>
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
            placeholder="Buscar por nombre..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className={styles.searchInput}
          />
          {isAdmin && (
            <button className={styles.btnNew} onClick={openCreate}>
              Nuevo Producto
            </button>
          )}
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-secondary)', fontSize: 14 }}>Cargando productos...</p>
        ) : productosFiltrados.length === 0 ? (
          <div className={styles.emptyState}>No se encontraron productos</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Cantidad</th>
                {isAdmin && <th>Acciones</th>}
              </tr>
            </thead>
            <tbody>
              {productosFiltrados.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.nombre}</td>
                  <td>{p.categoria}</td>
                  <td>${p.precio.toFixed(2)}</td>
                  <td>{p.cantidad}</td>
                  {isAdmin && (
                    <td>
                      <div className={styles.actions}>
                        <button className={styles.btnEdit} onClick={() => openEdit(p)}>
                          Editar
                        </button>
                        <button
                          className={styles.btnDelete}
                          onClick={() => setDeleteId(p.id)}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {showForm && (
          <div className={styles.overlay} onClick={closeForm}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
              <h2>{editingId ? 'Editar Producto' : 'Nuevo Producto'}</h2>
              <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                  <label>Nombre</label>
                  <input
                    type="text"
                    value={form.nombre}
                    onChange={(e) => handleInputChange('nombre', e.target.value)}
                  />
                  {fieldErrors.nombre && (
                    <div className={styles.fieldError}>{fieldErrors.nombre}</div>
                  )}
                </div>
                <div className={styles.formGroup}>
                  <label>Categoría</label>
                  <input
                    type="text"
                    value={form.categoria}
                    onChange={(e) => handleInputChange('categoria', e.target.value)}
                  />
                  {fieldErrors.categoria && (
                    <div className={styles.fieldError}>{fieldErrors.categoria}</div>
                  )}
                </div>
                <div className={styles.formGroup}>
                  <label>Precio</label>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    value={form.precio}
                    onChange={(e) => handleInputChange('precio', e.target.value)}
                  />
                  {fieldErrors.precio && (
                    <div className={styles.fieldError}>{fieldErrors.precio}</div>
                  )}
                </div>
                <div className={styles.formGroup}>
                  <label>Cantidad</label>
                  <input
                    type="number"
                    min="0"
                    value={form.cantidad}
                    onChange={(e) => handleInputChange('cantidad', e.target.value)}
                  />
                  {fieldErrors.cantidad && (
                    <div className={styles.fieldError}>{fieldErrors.cantidad}</div>
                  )}
                </div>
                <div className={styles.formActions}>
                  <button type="button" className={styles.btnCancel} onClick={closeForm}>
                    Cancelar
                  </button>
                  <button type="submit" className={styles.btnSave}>
                    {editingId ? 'Guardar Cambios' : 'Crear Producto'}
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
                ¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede
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
