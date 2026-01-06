/**
 * Equipment Type Definitions (Pumps and Sensors)
 */

export enum EquipmentStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  MAINTENANCE = 'MAINTENANCE',
}

export enum SensorType {
  TEMPERATURE = 'TEMPERATURE',
  HUMIDITY = 'HUMIDITY',
  SOIL_MOISTURE = 'SOIL_MOISTURE',
}

export interface ConnectedPump {
  id: string;
  farmId: string;
  model: string;
  status: EquipmentStatus;
  maxFlow: number;
  formattedMaxFlow: string;
  location: string;
  installationDate: string;
  lastMaintenanceDate: string | null;
  nextMaintenanceDate: string | null;
  maintenanceNotes: string | null;
  operational: boolean;
  maintenanceOverdue: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ConnectedSensor {
  id: string;
  farmId: string;
  type: SensorType;
  model: string;
  battery: number;
  batteryStatus: string;
  lastCommunication: string;
  location: string;
  installationDate: string;
  active: boolean;
  alertThreshold: number | null;
  batteryLow: boolean;
  batteryCritical: boolean;
  online: boolean;
  unit: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePumpRequest {
  farmId: string;
  model: string;
  status: EquipmentStatus;
  maxFlow: number;
  location: string;
  installationDate: string;
}

export interface UpdatePumpRequest {
  model?: string;
  status?: EquipmentStatus;
  maxFlow?: number;
  location?: string;
  lastMaintenanceDate?: string;
  nextMaintenanceDate?: string;
  maintenanceNotes?: string;
}

export interface CreateSensorRequest {
  farmId: string;
  type: SensorType;
  model: string;
  battery: number;
  location: string;
  installationDate: string;
  alertThreshold?: number;
}

export interface UpdateSensorRequest {
  type?: SensorType;
  model?: string;
  battery?: number;
  location?: string;
  active?: boolean;
  alertThreshold?: number;
  lastCommunication?: string;
}

export interface MaintenanceRequest {
  scheduledDate: string;
  notes?: string;
}

export interface FarmEquipmentReport {
  farmId: string;
  activePumps: number;
  inactivePumps: number;
  maintenancePumps: number;
  totalPumps: number;
  activeSensors: number;
  inactiveSensors: number;
  totalSensors: number;
  pumps: ConnectedPump[];
  sensors: ConnectedSensor[];
}
