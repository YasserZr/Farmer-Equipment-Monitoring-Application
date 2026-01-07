import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useFarmers, useCreateFarmer } from '@/hooks/use-farmers';
import { describe, it, expect, vi, beforeEach } from 'vitest';

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

describe('useFarmers Hook', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('fetches farmers successfully', async () => {
    const mockFarmers = {
      content: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          active: true,
        },
      ],
      totalElements: 1,
    };

    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockFarmers,
    });
    global.fetch = mockFetch;

    const { result } = renderHook(() => useFarmers({ page: 0, size: 10 }), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.content).toHaveLength(1);
    expect(result.current.data?.content[0].firstName).toBe('John');
  });

  it('handles fetch error correctly', async () => {
    const mockFetch = vi.fn().mockRejectedValue(new Error('API Error'));
    global.fetch = mockFetch;

    const { result } = renderHook(() => useFarmers({ page: 0, size: 10 }), {
      wrapper: createWrapper(),
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });

    expect(result.current.error).toBeDefined();
  });
});

describe('useCreateFarmer Hook', () => {
  it('creates farmer successfully', async () => {
    const newFarmer = {
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane@example.com',
      phone: '+1234567890',
      active: true,
    };

    const mockResponse = {
      id: 1,
      ...newFarmer,
    };

    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockResponse,
    });
    global.fetch = mockFetch;

    const { result } = renderHook(() => useCreateFarmer(), {
      wrapper: createWrapper(),
    });

    result.current.mutate(newFarmer);

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data?.id).toBe(1);
    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining('/api/farmers'),
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify(newFarmer),
      })
    );
  });

  it('handles creation error correctly', async () => {
    const mockFetch = vi.fn().mockRejectedValue(new Error('Creation failed'));
    global.fetch = mockFetch;

    const { result } = renderHook(() => useCreateFarmer(), {
      wrapper: createWrapper(),
    });

    result.current.mutate({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
    });

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });

    expect(result.current.error).toBeDefined();
  });
});
