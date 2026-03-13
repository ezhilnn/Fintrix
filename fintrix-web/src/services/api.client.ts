// ================================================================
// api.client.ts
// Axios instance wired to match Spring Security's JWT filter.
//
// JwtAuthenticationFilter.java reads:
//   request.getHeader("Authorization")
//   → expects "Bearer <token>"
//   → strips "Bearer " prefix (7 chars)
//
// GlobalExceptionHandler.java returns:
//   { success: false, message: "...", data: null/errors }
//   404 → ResourceNotFoundException
//   400 → MethodArgumentNotValidException  (data = Map<field, message>)
//   500 → generic error
// ================================================================

import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios';
import { API_BASE_URL, API_TIMEOUT_MS, AUTH_TOKEN_KEY } from '../utils/constants';
import type { ApiResponse, ApiError } from '../types/api.types';

// ── Create axios instance ────────────────────────────────────────
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT_MS,
  headers: {
    'Content-Type': 'application/json',
    'Accept':       'application/json',
  },
});

// ── Request interceptor — attach JWT ────────────────────────────
// Mirrors what JwtAuthenticationFilter expects:
//   Authorization: Bearer eyJhbGci...
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem(AUTH_TOKEN_KEY);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// ── Response interceptor — normalise errors ──────────────────────
// GlobalExceptionHandler sends:
//   400 Validation: { success: false, message: "Validation failed", data: { field: "msg" } }
//   404 Not Found:  { success: false, message: "User not found..." }
//   500 Server err: { success: false, message: "Something went wrong..." }
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (!error.response) {
      // Network error — backend not reachable
      const apiError: ApiError = {
        status:  0,
        message: 'Cannot connect to Fintrix server. Please check your connection.',
      };
      return Promise.reject(apiError);
    }

    const { status, data } = error.response;

    // Backend always wraps errors in ApiResponse shape
    const apiError: ApiError = {
      status,
      message: data?.message ?? 'Something went wrong. Please try again.',
      // 400 validation errors: data.data = { fieldName: "error message" }
      errors: status === 400 && data?.data ? data.data : undefined,
    };

    // 401 Unauthorized — JWT expired or invalid
    // Clear token and redirect to login
    if (status === 401) {
      localStorage.removeItem(AUTH_TOKEN_KEY);
      // Let authStore handle the redirect by dispatching a custom event
      window.dispatchEvent(new CustomEvent('fintrix:unauthorized'));
    }

    return Promise.reject(apiError);
  },
);

// ── Typed helper wrappers ────────────────────────────────────────
// These unwrap ApiResponse<T>.data so services get T directly

export const get = async <T>(url: string, params?: object): Promise<T> => {
  const res = await apiClient.get<ApiResponse<T>>(url, { params });
  return res.data.data;
};

export const post = async <T>(url: string, body?: unknown): Promise<T> => {
  const res = await apiClient.post<ApiResponse<T>>(url, body);
  return res.data.data;
};

export const put = async <T>(url: string, body?: unknown): Promise<T> => {
  const res = await apiClient.put<ApiResponse<T>>(url, body);
  return res.data.data;
};

export const del = async <T>(url: string): Promise<T> => {
  const res = await apiClient.delete<ApiResponse<T>>(url);
  return res.data.data;
};

export default apiClient;