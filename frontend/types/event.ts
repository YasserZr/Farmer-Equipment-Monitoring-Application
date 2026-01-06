/**
 * Supervision and Event Type Definitions
 */

export enum EventType {
  EQUIPMENT_CREATED = 'EQUIPMENT_CREATED',
  STATUS_CHANGED = 'STATUS_CHANGED',
  MAINTENANCE_SCHEDULED = 'MAINTENANCE_SCHEDULED',
  MAINTENANCE_COMPLETED = 'MAINTENANCE_COMPLETED',
  BATTERY_LOW = 'BATTERY_LOW',
  SENSOR_OFFLINE = 'SENSOR_OFFLINE',
  EQUIPMENT_FAILURE = 'EQUIPMENT_FAILURE',
  SYSTEM_ALERT = 'SYSTEM_ALERT',
}

export enum EventSeverity {
  INFO = 'INFO',
  WARNING = 'WARNING',
  CRITICAL = 'CRITICAL',
}

export interface EquipmentEvent {
  id: string;
  eventType: EventType;
  equipmentId: string;
  equipmentType: string;
  farmId: string;
  timestamp: string;
  payload: Record<string, any>;
  message: string;
  severity: EventSeverity;
  acknowledged: boolean;
  acknowledgedAt: string | null;
  acknowledgedBy: string | null;
  receivedAt: string;
  processed: boolean;
  processingNotes: string | null;
}

export interface EventFilterRequest {
  farmId?: string;
  equipmentId?: string;
  eventType?: EventType;
  severity?: EventSeverity;
  startDate?: string;
  endDate?: string;
  acknowledged?: boolean;
}

export interface AcknowledgeEventRequest {
  acknowledgedBy: string;
  notes?: string;
}

export interface DashboardStatistics {
  totalEvents: number;
  unacknowledgedEvents: number;
  criticalEvents: number;
  warningEvents: number;
  infoEvents: number;
  eventCountsByType: Record<string, number>;
  eventCountsBySeverity: Record<string, number>;
  recentCriticalEvents: EquipmentEvent[];
  recentEvents: EquipmentEvent[];
  dailyEventCounts: Record<string, number>;
  eventsLast24Hours: number;
  eventsLast7Days: number;
  eventsLast30Days: number;
}
