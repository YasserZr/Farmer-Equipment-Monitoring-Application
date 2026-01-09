/**
 * Farmer and Farm Type Definitions
 */

export interface Farmer {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: 'OWNER' | 'MANAGER' | 'WORKER';
  registrationDate: string;
  farmCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Farm {
  id: string;
  farmerId: string;
  name: string;
  location: string;
  size: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFarmerRequest {
  name: string;
  email: string;
  phone: string;
  role: 'OWNER' | 'MANAGER' | 'WORKER';
}

export interface UpdateFarmerRequest {
  name?: string;
  email?: string;
  phone?: string;
  role?: 'OWNER' | 'MANAGER' | 'WORKER';
}

export interface CreateFarmRequest {
  farmerId: string;
  name: string;
  location: string;
  size: number;
}

export interface UpdateFarmRequest {
  name?: string;
  location?: string;
  size?: number;
}

export interface FarmerStatistics {
  totalFarmers: number;
  activeFarmers: number;
  inactiveFarmers: number;
  totalFarms: number;
  averageFarmSize: number;
  recentFarmers: Farmer[];
}
