import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { FarmerForm } from '@/components/farmers/farmer-form';
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

describe('FarmerForm Component', () => {
  const mockOnSuccess = vi.fn();

  beforeEach(() => {
    mockOnSuccess.mockClear();
  });

  it('renders all form fields', () => {
    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/phone/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/address/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
  });

  it('displays validation errors for empty required fields', async () => {
    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    const submitButton = screen.getByRole('button', { name: /submit/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/first name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/last name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/email is required/i)).toBeInTheDocument();
    });

    expect(mockOnSuccess).not.toHaveBeenCalled();
  });

  it('displays validation error for invalid email format', async () => {
    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    const emailInput = screen.getByLabelText(/email/i);
    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.blur(emailInput);

    await waitFor(() => {
      expect(screen.getByText(/invalid email format/i)).toBeInTheDocument();
    });
  });

  it('submits form with valid data', async () => {
    const mockFetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
      }),
    });
    global.fetch = mockFetch;

    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    // Fill in the form
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' },
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: 'Doe' },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'john.doe@example.com' },
    });
    fireEvent.change(screen.getByLabelText(/phone/i), {
      target: { value: '+1234567890' },
    });

    // Submit the form
    const submitButton = screen.getByRole('button', { name: /submit/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockFetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/farmers'),
        expect.objectContaining({
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: expect.stringContaining('John'),
        })
      );
    });
  });

  it('displays error message on submission failure', async () => {
    const mockFetch = vi.fn().mockRejectedValue(new Error('API Error'));
    global.fetch = mockFetch;

    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    // Fill in required fields
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' },
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: 'Doe' },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'john.doe@example.com' },
    });

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: /submit/i }));

    await waitFor(() => {
      expect(screen.getByText(/error/i)).toBeInTheDocument();
    });

    expect(mockOnSuccess).not.toHaveBeenCalled();
  });

  it('pre-fills form when editing existing farmer', () => {
    const existingFarmer = {
      id: 1,
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane.smith@example.com',
      phone: '+9876543210',
      address: '456 Farm Ave',
      city: 'FarmTown',
      country: 'USA',
      active: true,
    };

    renderWithClient(
      <FarmerForm farmer={existingFarmer} onSuccess={mockOnSuccess} />
    );

    expect(screen.getByLabelText(/first name/i)).toHaveValue('Jane');
    expect(screen.getByLabelText(/last name/i)).toHaveValue('Smith');
    expect(screen.getByLabelText(/email/i)).toHaveValue('jane.smith@example.com');
    expect(screen.getByLabelText(/phone/i)).toHaveValue('+9876543210');
  });

  it('disables submit button while submitting', async () => {
    const mockFetch = vi.fn().mockImplementation(
      () =>
        new Promise((resolve) =>
          setTimeout(
            () =>
              resolve({
                ok: true,
                json: async () => ({ id: 1 }),
              }),
            100
          )
        )
    );
    global.fetch = mockFetch;

    renderWithClient(<FarmerForm onSuccess={mockOnSuccess} />);

    // Fill in required fields
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' },
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: 'Doe' },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'john.doe@example.com' },
    });

    const submitButton = screen.getByRole('button', { name: /submit/i });
    fireEvent.click(submitButton);

    expect(submitButton).toBeDisabled();

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
  });
});
