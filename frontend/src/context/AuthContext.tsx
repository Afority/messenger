import { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import type { AuthResponse, AuthUser } from '@/types';

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (login: string, password: string) => Promise<void>;
  register: (login: string, username: string, password: string) => Promise<void>;
  logout: () => void;
  error: string | null;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem('token');
  });
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const login = useCallback(async (loginStr: string, password: string) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const response = await fetch(`/api/auth/signin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: loginStr, password }),
      });

      if (!response.ok) {
        throw new Error(response.status === 401 ? 'Не верный логин или пароль' : 'Login failed, unknown error');
      }

      const data: AuthResponse = await response.json();
      
      setUser(data.user);
      setToken(data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      localStorage.setItem('token', data.token);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (loginStr: string, username: string, password: string) => {
    setIsLoading(true);
    setError(null);
    
    // Validation
    if (loginStr.length < 4 || loginStr.includes(' ')) {
      setError('Login must be at least 4 characters without spaces');
      setIsLoading(false);
      throw new Error('Login must be at least 4 characters without spaces');
    }
    
    if (username.length < 3) {
      setError('Username must be at least 3 characters');
      setIsLoading(false);
      throw new Error('Username must be at least 3 characters');
    }
    
    if (password.length < 8 || !/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/[0-9]/.test(password)) {
      setError('Password must be at least 8 characters with uppercase, lowercase, and number');
      setIsLoading(false);
      throw new Error('Password must be at least 8 characters with uppercase, lowercase, and number');
    }
    
    try {
      const response = await fetch(`/api/auth/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: loginStr, username, password }),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || 'Registration failed');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        login,
        register,
        logout,
        error,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
