'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'ADMIN' | 'VISUALIZADOR';
}

export default function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const { isAuthenticated, rol } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
    if (requiredRole && rol !== requiredRole) {
      router.push('/dashboard');
    }
  }, [isAuthenticated, rol, requiredRole, router]);

  if (!isAuthenticated) {
    return null;
  }

  if (requiredRole && rol !== requiredRole) {
    return null;
  }

  return <>{children}</>;
}
