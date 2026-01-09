import { useQuery } from '@tanstack/react-query';
import apiClient from '@/lib/api-client';
import { useFarmers } from './use-farmers';
import { usePumps, useSensors } from './use-equipment';
import { useEvents } from './use-events';

/**
 * Dashboard Statistics Hook
 * Combines data from multiple endpoints for dashboard overview
 */
export function useDashboardStats() {
  const { data: farmers } = useFarmers(0, 1000);
  const { data: pumps } = usePumps(0, 1000);
  const { data: sensors } = useSensors(0, 1000);
  const { data: events } = useEvents(0, 100);

  const totalFarmers = farmers?.totalElements || 0;
  const totalPumps = pumps?.totalElements || 0;
  const totalSensors = sensors?.totalElements || 0;
  const totalEquipment = totalPumps + totalSensors;

  const activePumps = pumps?.content?.filter(p => p.status === 'ACTIVE').length || 0;
  const activeSensors = sensors?.content?.filter(s => s.active).length || 0;
  const activeEquipment = activePumps + activeSensors;

  const lowBatterySensors = sensors?.content?.filter(s => s.batteryLow || s.batteryCritical).length || 0;
  const criticalSensors = sensors?.content?.filter(s => s.batteryCritical).length || 0;
  const offlineSensors = sensors?.content?.filter(s => !s.online).length || 0;

  const maintenancePumps = pumps?.content?.filter(p => p.status === 'MAINTENANCE').length || 0;
  const overduePumps = pumps?.content?.filter(p => p.maintenanceOverdue).length || 0;

  const criticalEvents = events?.content?.filter(e => e.severity === 'CRITICAL').length || 0;
  const warningEvents = events?.content?.filter(e => e.severity === 'WARNING').length || 0;
  const unacknowledgedEvents = events?.content?.filter(e => !e.acknowledged).length || 0;

  return {
    totalFarmers,
    totalEquipment,
    totalPumps,
    totalSensors,
    activeEquipment,
    activePumps,
    activeSensors,
    lowBatterySensors,
    criticalSensors,
    offlineSensors,
    maintenancePumps,
    overduePumps,
    criticalEvents,
    warningEvents,
    unacknowledgedEvents,
    recentEvents: events?.content?.slice(0, 10) || [],
  };
}

/**
 * Equipment Status Distribution Hook
 */
export function useEquipmentStatusDistribution() {
  const { data: pumps } = usePumps(0, 1000);
  const { data: sensors } = useSensors(0, 1000);

  const statusData = [
    {
      name: 'Active',
      pumps: pumps?.content?.filter(p => p.status === 'ACTIVE').length || 0,
      sensors: sensors?.content?.filter(s => s.active).length || 0,
    },
    {
      name: 'Inactive',
      pumps: pumps?.content?.filter(p => p.status === 'INACTIVE').length || 0,
      sensors: sensors?.content?.filter(s => !s.active).length || 0,
    },
    {
      name: 'Maintenance',
      pumps: pumps?.content?.filter(p => p.status === 'MAINTENANCE').length || 0,
      sensors: 0,
    },
  ];

  const equipmentTypeData = [
    { name: 'Pumps', value: pumps?.totalElements || 0 },
    { name: 'Sensors', value: sensors?.totalElements || 0 },
  ];

  return {
    statusData,
    equipmentTypeData,
  };
}

/**
 * Active Alerts Hook
 * Combines battery alerts, offline sensors, and overdue maintenance
 */
export function useActiveAlerts() {
  const { data: sensors } = useSensors(0, 1000);
  const { data: pumps } = usePumps(0, 1000);
  const { data: events } = useEvents(0, 100);

  const alerts: any[] = [];

  // Battery alerts
  sensors?.content?.forEach(sensor => {
    if (sensor.batteryCritical) {
      alerts.push({
        id: `battery-critical-${sensor.id}`,
        type: 'battery',
        equipmentId: sensor.id,
        equipmentType: 'Sensor',
        message: `Critical battery level (${sensor.battery}%)`,
        severity: 'high',
        timestamp: new Date().toISOString(),
      });
    } else if (sensor.batteryLow) {
      alerts.push({
        id: `battery-low-${sensor.id}`,
        type: 'battery',
        equipmentId: sensor.id,
        equipmentType: 'Sensor',
        message: `Low battery level (${sensor.battery}%)`,
        severity: 'medium',
        timestamp: new Date().toISOString(),
      });
    }
  });

  // Offline sensors
  sensors?.content?.forEach(sensor => {
    if (!sensor.online) {
      alerts.push({
        id: `offline-${sensor.id}`,
        type: 'offline',
        equipmentId: sensor.id,
        equipmentType: 'Sensor',
        message: 'Sensor is offline',
        severity: 'high',
        timestamp: sensor.lastCommunication,
      });
    }
  });

  // Overdue maintenance
  pumps?.content?.forEach(pump => {
    if (pump.maintenanceOverdue) {
      alerts.push({
        id: `maintenance-${pump.id}`,
        type: 'maintenance',
        equipmentId: pump.id,
        equipmentType: 'Pump',
        message: 'Maintenance overdue',
        severity: 'medium',
        timestamp: pump.nextMaintenanceDate || new Date().toISOString(),
      });
    }
  });

  // Critical events
  events?.content
    ?.filter(e => e.severity === 'CRITICAL' && !e.acknowledged)
    .slice(0, 5)
    .forEach(event => {
      alerts.push({
        id: `event-${event.id}`,
        type: 'critical',
        equipmentId: event.equipmentId,
        equipmentType: event.equipmentType || 'Equipment',
        message: event.message,
        severity: 'high',
        timestamp: event.timestamp,
      });
    });

  // Sort by severity and timestamp
  return alerts.sort((a, b) => {
    const severityOrder = { high: 0, medium: 1, low: 2 };
    if (severityOrder[a.severity as keyof typeof severityOrder] !== severityOrder[b.severity as keyof typeof severityOrder]) {
      return severityOrder[a.severity as keyof typeof severityOrder] - severityOrder[b.severity as keyof typeof severityOrder];
    }
    return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
  });
}

/**
 * Maintenance Schedule Hook
 */
export function useUpcomingMaintenance() {
  return useQuery<any[]>({
    queryKey: ['maintenance', 'upcoming'],
    queryFn: () => apiClient.get('/api/maintenance'),
    select: (data) => {
      return data
        .filter((item: any) => item.status !== 'COMPLETED')
        .sort((a: any, b: any) => new Date(a.scheduledDate).getTime() - new Date(b.scheduledDate).getTime());
    },
  });
}
