import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import FarmersPage from '@/app/farmers/page';
import { describe, it, expect, vi, beforeEach } from 'vitest';

const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

const renderWithClient = (component: React.ReactElement) => {
  const queryClient = createTestQueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      {component}
    </QueryClientProvider>
  );
};

describe('Farmers Page Integration Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders loading state initially', () => {
    const mockFetch = vi.fn().mockImplementation(
      () =>
        new Promise(() => {
          // Never resolves to keep loading state
        })
    );
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('renders farmers list after successful data fetch', async () => {
    const mockFarmers = {
      content: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john.doe@example.com',
          phone: '+1234567890',
          active: true,
        },
        {
          id: 2,
          firstName: 'Jane',
          lastName: 'Smith',
          email: 'jane.smith@example.com',
          phone: '+9876543210',
          active: true,
        },
      ],
      totalElements: 2,
      totalPages: 1,
    };

    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockFarmers,
    });
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });

    expect(screen.getByText('john.doe@example.com')).toBeInTheDocument();
    expect(screen.getByText('jane.smith@example.com')).toBeInTheDocument();
  });

  it('displays error message on data fetch failure', async () => {
    const mockFetch = vi.fn().mockRejectedValue(new Error('Network error'));
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    await waitFor(() => {
      expect(
        screen.getByText(/error loading farmers|failed to fetch/i)
      ).toBeInTheDocument();
    });
  });

  it('displays empty state when no farmers exist', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        content: [],
        totalElements: 0,
        totalPages: 0,
      }),
    });
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    await waitFor(() => {
      expect(
        screen.getByText(/no farmers found|add your first farmer/i)
      ).toBeInTheDocument();
    });
  });

  it('renders add farmer button', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        content: [],
        totalElements: 0,
        totalPages: 0,
      }),
    });
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    await waitFor(() => {
      expect(
        screen.getByRole('button', { name: /add farmer/i })
      ).toBeInTheDocument();
    });
  });

  it('handles pagination correctly', async () => {
    const mockPage1 = {
      content: [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          active: true,
        },
      ],
      totalElements: 15,
      totalPages: 2,
      number: 0,
    };

    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockPage1,
    });
    global.fetch = mockFetch;

    renderWithClient(<FarmersPage />);

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });

    // Check if pagination controls are rendered
    expect(screen.getByText(/page 1 of 2/i)).toBeInTheDocument();
  });
});
