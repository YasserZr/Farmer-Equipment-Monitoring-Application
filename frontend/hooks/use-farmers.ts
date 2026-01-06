import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/lib/api-client';
import type {
  Farmer,
  CreateFarmerRequest,
  UpdateFarmerRequest,
  Farm,
  CreateFarmRequest,
  UpdateFarmRequest,
  FarmerStatistics,
} from '@/types/farmer';
import type { PaginatedResponse } from '@/types/common';
import { toast } from 'sonner';

/**
 * Custom Hooks for Farmers API
 */

const FARMERS_KEY = 'farmers';
const FARMS_KEY = 'farms';

// Fetch all farmers with pagination
export function useFarmers(page = 0, size = 10) {
  return useQuery<PaginatedResponse<Farmer>>({
    queryKey: [FARMERS_KEY, page, size],
    queryFn: () => apiClient.get(`/api/farmers?page=${page}&size=${size}`),
  });
}

// Fetch single farmer by ID
export function useFarmer(id: string) {
  return useQuery<Farmer>({
    queryKey: [FARMERS_KEY, id],
    queryFn: () => apiClient.get(`/api/farmers/${id}`),
    enabled: !!id,
  });
}

// Create new farmer
export function useCreateFarmer() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFarmerRequest) =>
      apiClient.post<Farmer, CreateFarmerRequest>('/api/farmers', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [FARMERS_KEY] });
      toast.success('Farmer created successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create farmer');
    },
  });
}

// Update farmer
export function useUpdateFarmer() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateFarmerRequest }) =>
      apiClient.put<Farmer, UpdateFarmerRequest>(`/api/farmers/${id}`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [FARMERS_KEY] });
      queryClient.invalidateQueries({ queryKey: [FARMERS_KEY, variables.id] });
      toast.success('Farmer updated successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to update farmer');
    },
  });
}

// Delete farmer
export function useDeleteFarmer() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => apiClient.del(`/api/farmers/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [FARMERS_KEY] });
      toast.success('Farmer deleted successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to delete farmer');
    },
  });
}

// Fetch farms for a specific farmer
export function useFarms(farmerId: string, page = 0, size = 10) {
  return useQuery<PaginatedResponse<Farm>>({
    queryKey: [FARMS_KEY, farmerId, page, size],
    queryFn: () => apiClient.get(`/api/farmers/${farmerId}/farms?page=${page}&size=${size}`),
    enabled: !!farmerId,
  });
}

// Fetch single farm
export function useFarm(farmerId: string, farmId: string) {
  return useQuery<Farm>({
    queryKey: [FARMS_KEY, farmerId, farmId],
    queryFn: () => apiClient.get(`/api/farmers/${farmerId}/farms/${farmId}`),
    enabled: !!farmerId && !!farmId,
  });
}

// Create new farm
export function useCreateFarm(farmerId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFarmRequest) =>
      apiClient.post<Farm, CreateFarmRequest>(`/api/farmers/${farmerId}/farms`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [FARMS_KEY, farmerId] });
      toast.success('Farm created successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create farm');
    },
  });
}

// Update farm
export function useUpdateFarm(farmerId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ farmId, data }: { farmId: string; data: UpdateFarmRequest }) =>
      apiClient.put<Farm, UpdateFarmRequest>(`/api/farmers/${farmerId}/farms/${farmId}`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [FARMS_KEY, farmerId] });
      queryClient.invalidateQueries({ queryKey: [FARMS_KEY, farmerId, variables.farmId] });
      toast.success('Farm updated successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to update farm');
    },
  });
}

// Delete farm
export function useDeleteFarm(farmerId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (farmId: string) => apiClient.del(`/api/farmers/${farmerId}/farms/${farmId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [FARMS_KEY, farmerId] });
      toast.success('Farm deleted successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to delete farm');
    },
  });
}

// Fetch farmer statistics
export function useFarmerStatistics() {
  return useQuery<FarmerStatistics>({
    queryKey: [FARMERS_KEY, 'statistics'],
    queryFn: () => apiClient.get('/api/farmers/statistics'),
  });
}
