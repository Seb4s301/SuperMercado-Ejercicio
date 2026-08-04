'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { href: '/dashboard', label: 'Dashboard', icon: '📊' },
  { href: '/productos', label: 'Productos', icon: '📦' },
  { href: '/sucursales', label: 'Sucursales', icon: '🏪' },
  { href: '/ventas', label: 'Ventas', icon: '🛒' },
];

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { username, rol, logout } = useAuth();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <h2>Supermercado</h2>
        <p>Sistema de gestión</p>
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <Link
            key={item.href}
            href={item.href}
            className={`sidebar-link ${pathname === item.href ? 'active' : ''}`}
          >
            <span className="sidebar-icon">{item.icon}</span>
            {item.label}
          </Link>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-user">
          <div className="sidebar-user-avatar">
            {username?.charAt(0).toUpperCase() || 'A'}
          </div>
          <div className="sidebar-user-info">
            <div className="sidebar-user-name">{username || 'Admin'}</div>
            <div className="sidebar-user-role">{rol || 'ADMIN'}</div>
          </div>
        </div>
        <button className="btn-logout" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </div>
    </aside>
  );
}
