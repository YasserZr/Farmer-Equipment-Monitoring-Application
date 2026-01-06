/**
 * Farmer and Farm Type Definitions
 */

export interface Farmer {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  dateOfBirth: string;
  active: boolean;
  registrationDate: string;
  lastModified: string;
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
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  address: string;
  dateOfBirth: string;
}

export interface UpdateFarmerRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  dateOfBirth?: string;
  active?: boolean;
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
