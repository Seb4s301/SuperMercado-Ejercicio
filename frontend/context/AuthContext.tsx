'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface AuthContextType {
  token: string | null;
  rol: string | null;
  username: string | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (token: string, rol: string, username: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [rol, setRol] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedRol = localStorage.getItem('rol');
    const savedUsername = localStorage.getItem('username');
    if (savedToken) {
      setToken(savedToken);
      setRol(savedRol);
      setUsername(savedUsername);
    }
  }, []);

  const handleLogin = (newToken: string, newRol: string, newUsername: string) => {
    localStorage.setItem('token', newToken);
    localStorage.setItem('rol', newRol);
    localStorage.setItem('username', newUsername);
    setToken(newToken);
    setRol(newRol);
    setUsername(newUsername);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('username');
    setToken(null);
    setRol(null);
    setUsername(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        rol,
        username,
        isAuthenticated: !!token,
        isAdmin: rol === 'ADMIN',
        login: handleLogin,
        logout: handleLogout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
}
