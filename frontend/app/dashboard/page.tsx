'use client';

import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import MainLayout from '../../components/MainLayout';
import ProtectedRoute from '../../components/ProtectedRoute';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar, Line, Doughnut } from 'react-chartjs-2';
import styles from './dashboard.module.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

interface VentaPorSucursal {
  nombreSucursal: string;
  cantidadVentas: number;
}

interface ProductoMasVendido {
  nombreProducto: string;
  unidadesVendidas: number;
}

interface VentaPorMes {
  mes: string;
  cantidadVentas: number;
  ingreso: number;
}

interface DashboardData {
  totalProductos: number;
  totalSucursales: number;
  totalVentas: number;
  ingresoTotal: number;
  ventasPorSucursal: VentaPorSucursal[];
  productosMasVendidos: ProductoMasVendido[];
  ventasPorMes: VentaPorMes[];
}

export default function DashboardPage() {
  const { token } = useAuth();
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDashboard = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch('/api/dashboard', {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error('Error al cargar el dashboard');
      const json = await res.json();
      setData(json);
    } catch (err: any) {
      setError(err.message || 'Error desconocido');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) fetchDashboard();
  }, [token]);

  const barData = {
    labels: data?.ventasPorSucursal.map((v) => v.nombreSucursal) || [],
    datasets: [
      {
        label: 'Ventas',
        data: data?.ventasPorSucursal.map((v) => v.cantidadVentas) || [],
        backgroundColor: 'rgba(37, 99, 235, 0.7)',
        borderRadius: 6,
        maxBarThickness: 48,
      },
    ],
  };

  const lineData = {
    labels: data?.ventasPorMes.map((v) => v.mes) || [],
    datasets: [
      {
        label: 'Ingresos',
        data: data?.ventasPorMes.map((v) => v.ingreso) || [],
        borderColor: 'rgba(34, 197, 94, 1)',
        backgroundColor: 'rgba(34, 197, 94, 0.1)',
        fill: true,
        tension: 0.3,
        pointRadius: 4,
        pointBackgroundColor: 'rgba(34, 197, 94, 1)',
      },
    ],
  };

  const doughnutData = {
    labels:
      data?.productosMasVendidos.slice(0, 5).map((p) => p.nombreProducto) || [],
    datasets: [
      {
        data:
          data?.productosMasVendidos
            .slice(0, 5)
            .map((p) => p.unidadesVendidas) || [],
        backgroundColor: [
          'rgba(37, 99, 235, 0.8)',
          'rgba(34, 197, 94, 0.8)',
          'rgba(245, 158, 11, 0.8)',
          'rgba(239, 68, 68, 0.8)',
          'rgba(139, 92, 246, 0.8)',
        ],
        borderWidth: 0,
      },
    ],
  };

  const formatCurrency = (value: number) =>
    `$${value.toLocaleString('es-AR', { minimumFractionDigits: 2 })}`;

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="page-header">
          <h1>Dashboard</h1>
          <p>Estadísticas del supermercado</p>
        </div>

        {loading && (
          <div className={styles.loadingContainer}>
            <div className={styles.spinner} />
            <p className={styles.loadingText}>Cargando datos...</p>
          </div>
        )}

        {error && (
          <div className={styles.errorContainer}>
            <p className={styles.errorText}>{error}</p>
            <button className={styles.retryButton} onClick={fetchDashboard}>
              Reintentar
            </button>
          </div>
        )}

        {data && !loading && (
          <>
            <div className={styles.summaryGrid}>
              <div className={styles.summaryCard}>
                <div className={`${styles.cardIcon} ${styles.cardIconBlue}`}>
                  📦
                </div>
                <div className={styles.cardInfo}>
                  <span className={styles.cardLabel}>Productos</span>
                  <span className={styles.cardValue}>{data.totalProductos}</span>
                </div>
              </div>
              <div className={styles.summaryCard}>
                <div className={`${styles.cardIcon} ${styles.cardIconPurple}`}>
                  🏪
                </div>
                <div className={styles.cardInfo}>
                  <span className={styles.cardLabel}>Sucursales</span>
                  <span className={styles.cardValue}>
                    {data.totalSucursales}
                  </span>
                </div>
              </div>
              <div className={styles.summaryCard}>
                <div className={`${styles.cardIcon} ${styles.cardIconGreen}`}>
                  🛒
                </div>
                <div className={styles.cardInfo}>
                  <span className={styles.cardLabel}>Ventas totales</span>
                  <span className={styles.cardValue}>{data.totalVentas}</span>
                </div>
              </div>
              <div className={styles.summaryCard}>
                <div className={`${styles.cardIcon} ${styles.cardIconOrange}`}>
                  💰
                </div>
                <div className={styles.cardInfo}>
                  <span className={styles.cardLabel}>Ingresos</span>
                  <span className={styles.cardValue}>
                    {formatCurrency(data.ingresoTotal)}
                  </span>
                </div>
              </div>
            </div>

            <div className={styles.chartsGrid}>
              <div className={styles.chartCard}>
                <h3 className={styles.chartTitle}>Ventas por sucursal</h3>
                <div className={styles.chartContainer}>
                  <Bar
                    data={barData}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      plugins: { legend: { display: false } },
                      scales: {
                        y: { beginAtZero: true, ticks: { stepSize: 1 } },
                      },
                    }}
                  />
                </div>
              </div>
              <div className={styles.chartCard}>
                <h3 className={styles.chartTitle}>Ingresos por mes</h3>
                <div className={styles.chartContainer}>
                  <Line
                    data={lineData}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      plugins: { legend: { display: false } },
                      scales: {
                        y: {
                          beginAtZero: true,
                          ticks: {
                            callback: (value) =>
                              `$${Number(value).toLocaleString()}`,
                          },
                        },
                      },
                    }}
                  />
                </div>
              </div>
            </div>

            <div className={styles.chartsGrid}>
              <div className={styles.chartCard}>
                <h3 className={styles.chartTitle}>
                  Top 5 productos más vendidos
                </h3>
                <div className={styles.chartContainer}>
                  <Doughnut
                    data={doughnutData}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      plugins: {
                        legend: {
                          position: 'bottom',
                          labels: { padding: 16, usePointStyle: true },
                        },
                      },
                    }}
                  />
                </div>
              </div>
            </div>
          </>
        )}
      </MainLayout>
    </ProtectedRoute>
  );
}
