import { useAuthStore } from '@/lib/store/auth-store';
import { useRouter } from 'next/navigation';
import { useCallback } from 'react';

/**
 * Custom Hook for Authentication
 */

export function useAuth() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading, login, logout, setLoading } = useAuthStore();

  const handleLogout = useCallback(() => {
    logout();
    router.push('/login');
  }, [logout, router]);

  return {
    user,
    isAuthenticated,
    isLoading,
    login,
    logout: handleLogout,
    setLoading,
  };
}
