import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import apiClient from '@/lib/api-client';
import type {
  EquipmentEvent,
  EventFilterRequest,
  AcknowledgeEventRequest,
  DashboardStatistics,
  EventType,
  EventSeverity,
} from '@/types/event';
import type { PaginatedResponse } from '@/types/common';
import { toast } from 'sonner';

/**
 * Custom Hooks for Events and Supervision API
 */

const EVENTS_KEY = 'events';
const STATISTICS_KEY = 'statistics';

// Fetch all events with pagination
export function useEvents(page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, page, size],
    queryFn: () => apiClient.get(`/api/events?page=${page}&size=${size}`),
  });
}

// Fetch single event
export function useEvent(id: string) {
  return useQuery<EquipmentEvent>({
    queryKey: [EVENTS_KEY, id],
    queryFn: () => apiClient.get(`/api/events/${id}`),
    enabled: !!id,
  });
}

// Fetch events by equipment
export function useEventsByEquipment(equipmentId: string, page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'equipment', equipmentId, page, size],
    queryFn: () => apiClient.get(`/api/events/equipment/${equipmentId}?page=${page}&size=${size}`),
    enabled: !!equipmentId,
  });
}

// Fetch events by farm
export function useEventsByFarm(farmId: string, page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'farm', farmId, page, size],
    queryFn: () => apiClient.get(`/api/events/farm/${farmId}?page=${page}&size=${size}`),
    enabled: !!farmId,
  });
}

// Fetch events by type
export function useEventsByType(eventType: EventType, page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'type', eventType, page, size],
    queryFn: () => apiClient.get(`/api/events/type/${eventType}?page=${page}&size=${size}`),
    enabled: !!eventType,
  });
}

// Fetch events by severity
export function useEventsBySeverity(severity: EventSeverity, page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'severity', severity, page, size],
    queryFn: () => apiClient.get(`/api/events/severity/${severity}?page=${page}&size=${size}`),
    enabled: !!severity,
  });
}

// Fetch unacknowledged events
export function useUnacknowledgedEvents(page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'unacknowledged', page, size],
    queryFn: () => apiClient.get(`/api/events/unacknowledged?page=${page}&size=${size}`),
    refetchInterval: 30000, // Refresh every 30 seconds
  });
}

// Filter events
export function useFilterEvents(filter: EventFilterRequest, page = 0, size = 20) {
  return useQuery<PaginatedResponse<EquipmentEvent>>({
    queryKey: [EVENTS_KEY, 'filter', filter, page, size],
    queryFn: () =>
      apiClient.post<PaginatedResponse<EquipmentEvent>, EventFilterRequest>(
        `/api/events/filter?page=${page}&size=${size}`,
        filter
      ),
    enabled: Object.keys(filter).length > 0,
  });
}

// Acknowledge event
export function useAcknowledgeEvent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: AcknowledgeEventRequest }) =>
      apiClient.post<EquipmentEvent, AcknowledgeEventRequest>(`/api/events/${id}/acknowledge`, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [EVENTS_KEY] });
      queryClient.invalidateQueries({ queryKey: [EVENTS_KEY, variables.id] });
      queryClient.invalidateQueries({ queryKey: [STATISTICS_KEY] });
      toast.success('Event acknowledged successfully');
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to acknowledge event');
    },
  });
}

// Fetch dashboard statistics
export function useDashboardStatistics() {
  return useQuery<DashboardStatistics>({
    queryKey: [STATISTICS_KEY, 'dashboard'],
    queryFn: () => apiClient.get('/api/statistics/dashboard'),
    refetchInterval: 60000, // Refresh every minute
  });
}

// Fetch event count by type
export function useEventCountByType() {
  return useQuery<Record<EventType, number>>({
    queryKey: [STATISTICS_KEY, 'by-type'],
    queryFn: () => apiClient.get('/api/statistics/events/by-type'),
  });
}

// Fetch event count by severity
export function useEventCountBySeverity() {
  return useQuery<Record<EventSeverity, number>>({
    queryKey: [STATISTICS_KEY, 'by-severity'],
    queryFn: () => apiClient.get('/api/statistics/events/by-severity'),
  });
}

// Fetch recent critical events
export function useRecentCriticalEvents(limit = 10) {
  return useQuery<EquipmentEvent[]>({
    queryKey: [EVENTS_KEY, 'recent-critical', limit],
    queryFn: () => apiClient.get(`/api/events/recent-critical?limit=${limit}`),
    refetchInterval: 30000,
  });
}
