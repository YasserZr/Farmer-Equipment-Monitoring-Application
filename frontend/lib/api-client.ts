import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ErrorResponse } from '@/types/common';

/**
 * API Client Configuration
 * 
 * This module creates and configures an Axios instance for API calls
 * with interceptors for authentication, error handling, and logging.
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

// Create Axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor
apiClient.interceptors.request.use(
  (config) => {
    // Add authentication token if available
    const token = getAuthToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Add farmer ID from session/storage if available
    const farmerId = getFarmerId();
    if (farmerId) {
      config.headers['X-Farmer-Id'] = farmerId;
    }

    // Log request in development
    if (process.env.NODE_ENV === 'development') {
      console.log('🚀 API Request:', {
        method: config.method?.toUpperCase(),
        url: config.url,
        data: config.data,
      });
    }

    return config;
  },
  (error: AxiosError) => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// Response Interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // Log response in development
    if (process.env.NODE_ENV === 'development') {
      console.log('✅ API Response:', {
        status: response.status,
        url: response.config.url,
        data: response.data,
      });
    }

    return response;
  },
  (error: AxiosError<ErrorResponse>) => {
    // Handle different error scenarios
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;

      console.error('❌ API Error:', {
        status,
        message: data?.message || error.message,
        path: data?.path,
        validationErrors: data?.validationErrors,
      });

      // Handle specific error codes
      switch (status) {
        case 401:
          // Unauthorized - clear auth and redirect to login
          handleUnauthorized();
          break;
        case 403:
          // Forbidden - insufficient permissions
          console.error('Forbidden: Insufficient permissions');
          break;
        case 404:
          // Not Found
          console.error('Resource not found');
          break;
        case 500:
          // Server Error
          console.error('Internal server error');
          break;
        case 503:
          // Service Unavailable
          console.error('Service temporarily unavailable');
          break;
        default:
          console.error('API Error:', data?.message || 'Unknown error occurred');
      }
    } else if (error.request) {
      // Request made but no response received
      console.error('❌ Network Error: No response received', error.request);
    } else {
      // Error in request configuration
      console.error('❌ Request Setup Error:', error.message);
    }

    return Promise.reject(error);
  }
);

// Helper Functions

/**
 * Get authentication token from storage
 */
function getAuthToken(): string | null {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('auth_token');
  }
  return null;
}

/**
 * Set authentication token in storage
 */
export function setAuthToken(token: string): void {
  if (typeof window !== 'undefined') {
    localStorage.setItem('auth_token', token);
  }
}

/**
 * Remove authentication token from storage
 */
export function removeAuthToken(): void {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('farmer_id');
  }
}

/**
 * Get farmer ID from storage
 */
function getFarmerId(): string | null {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('farmer_id');
  }
  return null;
}

/**
 * Set farmer ID in storage
 */
export function setFarmerId(farmerId: string): void {
  if (typeof window !== 'undefined') {
    localStorage.setItem('farmer_id', farmerId);
  }
}

/**
 * Handle unauthorized access
 */
function handleUnauthorized(): void {
  removeAuthToken();
  if (typeof window !== 'undefined') {
    window.location.href = '/login';
  }
}

/**
 * Generic GET request
 */
export async function get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await apiClient.get<T>(url, config);
  return response.data;
}

/**
 * Generic POST request
 */
export async function post<T, D = any>(
  url: string,
  data?: D,
  config?: AxiosRequestConfig
): Promise<T> {
  const response = await apiClient.post<T>(url, data, config);
  return response.data;
}

/**
 * Generic PUT request
 */
export async function put<T, D = any>(
  url: string,
  data?: D,
  config?: AxiosRequestConfig
): Promise<T> {
  const response = await apiClient.put<T>(url, data, config);
  return response.data;
}

/**
 * Generic PATCH request
 */
export async function patch<T, D = any>(
  url: string,
  data?: D,
  config?: AxiosRequestConfig
): Promise<T> {
  const response = await apiClient.patch<T>(url, data, config);
  return response.data;
}

/**
 * Generic DELETE request
 */
export async function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await apiClient.delete<T>(url, config);
  return response.data;
}

export default apiClient;
