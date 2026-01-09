import { useAuthStore } from '@/lib/store/auth-store';
import { useRouter } from 'next/navigation';
import { useCallback, useEffect, useState } from 'react';

/**
 * Custom Hook for Authentication
 */

export function useAuth() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading, login, logout, setLoading, hasHydrated } = useAuthStore();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  const handleLogout = useCallback(() => {
    logout();
    router.push('/login');
  }, [logout, router]);

  // Wait for both mounting and hydration
  const effectiveIsLoading = !mounted || !hasHydrated || isLoading;

  return {
    user,
    isAuthenticated,
    isLoading: effectiveIsLoading,
    login,
    logout: handleLogout,
    setLoading,
  };
}
