'use client';

import { useState, useEffect, FormEvent } from 'react';
import MainLayout from '../../components/MainLayout';
import ProtectedRoute from '../../components/ProtectedRoute';
import { useAuth } from '../../context/AuthContext';
import {
  Sucursal,
  getSucursales,
  createSucursal,
  updateSucursal,
  deleteSucursal,
} from '../../services/sucursalService';
import styles from './sucursales.module.css';

const initialForm = { nombre: '', direccion: '' };

export default function SucursalesPage() {
  const { token, isAdmin } = useAuth();

  const [sucursales, setSucursales] = useState<Sucursal[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState(initialForm);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    if (token) loadSucursales();
  }, [token]);

  async function loadSucursales() {
    try {
      setLoading(true);
      const data = await getSucursales(token!);
      setSucursales(data);
    } catch {
      setMessage({ type: 'error', text: 'Error al cargar sucursales' });
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

  function openEdit(s: Sucursal) {
    setEditingId(s.id);
    setForm({
      nombre: s.nombre,
      direccion: s.direccion,
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
      direccion: form.direccion,
    };

    try {
      if (editingId) {
        await updateSucursal(token!, editingId, body);
        setMessage({ type: 'success', text: 'Sucursal actualizada correctamente' });
      } else {
        await createSucursal(token!, body);
        setMessage({ type: 'success', text: 'Sucursal creada correctamente' });
      }
      closeForm();
      await loadSucursales();
    } catch (err: any) {
      if (err?.type === 'validation') {
        setFieldErrors(err.errors);
      } else {
        setMessage({ type: 'error', text: err.message || 'Error al guardar sucursal' });
      }
    }
  }

  async function confirmDelete() {
    if (!deleteId) return;
    try {
      await deleteSucursal(token!, deleteId);
      setMessage({ type: 'success', text: 'Sucursal eliminada correctamente' });
      setDeleteId(null);
      await loadSucursales();
    } catch (err: any) {
      setMessage({ type: 'error', text: err.message || 'Error al eliminar sucursal' });
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

  const sucursalesFiltradas = sucursales.filter((s) =>
    s.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="page-header">
          <h1>Sucursales</h1>
          <p>Gestión de sucursales del supermercado</p>
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
              Nueva Sucursal
            </button>
          )}
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-secondary)', fontSize: 14 }}>Cargando sucursales...</p>
        ) : sucursalesFiltradas.length === 0 ? (
          <div className={styles.emptyState}>No se encontraron sucursales</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Dirección</th>
                {isAdmin && <th>Acciones</th>}
              </tr>
            </thead>
            <tbody>
              {sucursalesFiltradas.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td>{s.nombre}</td>
                  <td>{s.direccion}</td>
                  {isAdmin && (
                    <td>
                      <div className={styles.actions}>
                        <button className={styles.btnEdit} onClick={() => openEdit(s)}>
                          Editar
                        </button>
                        <button
                          className={styles.btnDelete}
                          onClick={() => setDeleteId(s.id)}
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
              <h2>{editingId ? 'Editar Sucursal' : 'Nueva Sucursal'}</h2>
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
                  <label>Dirección</label>
                  <input
                    type="text"
                    value={form.direccion}
                    onChange={(e) => handleInputChange('direccion', e.target.value)}
                  />
                  {fieldErrors.direccion && (
                    <div className={styles.fieldError}>{fieldErrors.direccion}</div>
                  )}
                </div>
                <div className={styles.formActions}>
                  <button type="button" className={styles.btnCancel} onClick={closeForm}>
                    Cancelar
                  </button>
                  <button type="submit" className={styles.btnSave}>
                    {editingId ? 'Guardar Cambios' : 'Crear Sucursal'}
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
                ¿Estás seguro de que deseas eliminar esta sucursal? Esta acción no se puede
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
