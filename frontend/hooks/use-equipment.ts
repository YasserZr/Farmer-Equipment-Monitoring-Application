import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/lib/api-client';
import type {
  ConnectedPump,
  ConnectedSensor,
  CreatePumpRequest,
  UpdatePumpRequest,
  CreateSensorRequest,
  UpdateSensorRequest,
  MaintenanceRequest,
  FarmEquipmentReport,
} from '@/types/equipment';
import type { PaginatedResponse } from '@/types/common';
import { toast } from 'sonner';

/**
 * Custom Hooks for Equipment API
 */

const PUMPS_KEY = 'pumps';
const SENSORS_KEY = 'sensors';
const EQUIPMENT_KEY = 'equipment';

// Fetch all pumps
export function usePumps(page = 0, size = 10) {
  return useQuery<PaginatedResponse<ConnectedPump>>({
    queryKey: [PUMPS_KEY, page, size],
    queryFn: () => apiClient.get(`/api/pumps?page=${page}&size=${size}`),
  });
}

// Fetch single pump
export function usePump(id: string) {
  return useQuery<ConnectedPump>({
    queryKey: [PUMPS_KEY, id],
    queryFn: () => apiClient.get(`/api/pumps/${id}`),
    enabled: !!id,
  });
}

// Fetch pumps by farm
export function usePumpsByFarm(farmId: string) {
  return useQuery<ConnectedPump[]>({
    queryKey: [PUMPS_KEY, 'farm', farmId],
    queryFn: () => apiClient.get(`/api/pumps/farm/${farmId}`),
    enabled: !!farmId,
  });
}

// Create new pump
export function useCreatePump() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreatePumpRequest) =>
      apiClient.post<ConnectedPump, CreatePumpRequest>('/api/pumps', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Pump created successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create pump');
    },
  });
}

// Update pump
export function useUpdatePump() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdatePumpRequest }) =>
      apiClient.put<ConnectedPump, UpdatePumpRequest>(`/api/pumps/${id}`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY] });
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY, variables.id] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Pump updated successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to update pump');
    },
  });
}

// Delete pump
export function useDeletePump() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => apiClient.delete(`/api/pumps/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Pump deleted successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to delete pump');
    },
  });
}

// Schedule maintenance for pump
export function useScheduleMaintenance() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: MaintenanceRequest }) =>
      apiClient.post<ConnectedPump, MaintenanceRequest>(`/api/pumps/${id}/maintenance`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY, variables.id] });
      queryClient.invalidateQueries({ queryKey: [PUMPS_KEY] });
      toast.success('Maintenance scheduled successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to schedule maintenance');
    },
  });
}

// Fetch all sensors
export function useSensors(page = 0, size = 10) {
  return useQuery<PaginatedResponse<ConnectedSensor>>({
    queryKey: [SENSORS_KEY, page, size],
    queryFn: () => apiClient.get(`/api/sensors?page=${page}&size=${size}`),
  });
}

// Fetch single sensor
export function useSensor(id: string) {
  return useQuery<ConnectedSensor>({
    queryKey: [SENSORS_KEY, id],
    queryFn: () => apiClient.get(`/api/sensors/${id}`),
    enabled: !!id,
  });
}

// Fetch sensors by farm
export function useSensorsByFarm(farmId: string) {
  return useQuery<ConnectedSensor[]>({
    queryKey: [SENSORS_KEY, 'farm', farmId],
    queryFn: () => apiClient.get(`/api/sensors/farm/${farmId}`),
    enabled: !!farmId,
  });
}

// Fetch sensors by type
export function useSensorsByType(type: string) {
  return useQuery<ConnectedSensor[]>({
    queryKey: [SENSORS_KEY, 'type', type],
    queryFn: () => apiClient.get(`/api/sensors/type/${type}`),
    enabled: !!type,
  });
}

// Create new sensor
export function useCreateSensor() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateSensorRequest) =>
      apiClient.post<ConnectedSensor, CreateSensorRequest>('/api/sensors', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [SENSORS_KEY] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Sensor created successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create sensor');
    },
  });
}

// Update sensor
export function useUpdateSensor() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateSensorRequest }) =>
      apiClient.put<ConnectedSensor, UpdateSensorRequest>(`/api/sensors/${id}`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [SENSORS_KEY] });
      queryClient.invalidateQueries({ queryKey: [SENSORS_KEY, variables.id] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Sensor updated successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to update sensor');
    },
  });
}

// Delete sensor
export function useDeleteSensor() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => apiClient.delete(`/api/sensors/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [SENSORS_KEY] });
      queryClient.invalidateQueries({ queryKey: [EQUIPMENT_KEY] });
      toast.success('Sensor deleted successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to delete sensor');
    },
  });
}

// Fetch farm equipment report
export function useFarmEquipmentReport(farmId: string) {
  return useQuery<FarmEquipmentReport>({
    queryKey: [EQUIPMENT_KEY, 'report', farmId],
    queryFn: () => apiClient.get(`/api/equipment/farm/${farmId}/report`),
    enabled: !!farmId,
  });
}

// Fetch maintenance schedule
export function useMaintenanceSchedule() {
  return useQuery<any[]>({
    queryKey: ['maintenance', 'schedule'],
    queryFn: () => apiClient.get('/api/maintenance'),
  });
}

// Fetch equipment statistics
export function useEquipmentStatistics() {
  return useQuery<any>({
    queryKey: [EQUIPMENT_KEY, 'statistics'],
    queryFn: () => apiClient.get('/api/equipment/statistics'),
  });
}
