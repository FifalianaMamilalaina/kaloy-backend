import type { AxiosRequestConfig } from 'axios'
import axios, { AxiosError } from 'axios'
import RestResponse from '@/models/api/RestResponseModel.ts'
const instance = axios.create({
  baseURL: import.meta.env.VITE_APP_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default function api<T>() {
  async function request(config: AxiosRequestConfig): Promise<RestResponse<T>> {
    try {
      const response = await instance.request(config)
      return RestResponse.fromJson<T>(response.data)
    } catch (error: unknown) {
      if ((error as AxiosError).response) {
        return RestResponse.fromJson<T>((error as AxiosError).response?.data)
      }
      throw error
    }
  }

  const GET = (url: string, config?: AxiosRequestConfig) => {
    return request({ ...config, method: 'get', url })
  }

  const POST = (url: string, data?: unknown, config?: AxiosRequestConfig) => {
    return request({ ...config, method: 'post', url, data })
  }

  const PUT = (url: string, data?: unknown, config?: AxiosRequestConfig) => {
    return request({ ...config, method: 'put', url, data })
  }

  const DELETE = (url: string, config?: AxiosRequestConfig) => {
    return request({ ...config, method: 'delete', url })
  }

  const PATCH = (url: string, data?: unknown, config?: AxiosRequestConfig) => {
    return request({ ...config, method: 'patch', url, data })
  }

  return {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
  }
}
