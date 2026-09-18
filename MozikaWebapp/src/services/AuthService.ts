import api from '@/services/api'
import RestResponse from '@/models/api/RestResponseModel'
import type {
  RegisterPayload,
  RegisterResult,
  LoginPayload,
  LoginResult,
  MeResult,
  OtpSendResult,
  OtpVerifyResult,
  ChangeContactPayload,
} from '@/models/auth/AuthModels'

const BASE_URL = '/api/auth'

export async function register(payload: RegisterPayload) {
  const restApi = api<RegisterResult>()
  const response = await restApi.POST(`${BASE_URL}/register`, payload)
  return RestResponse.handleDataResponse<RegisterResult>(response)
}

export async function login(payload: LoginPayload) {
  const restApi = api<LoginResult>()
  const response = await restApi.POST(`${BASE_URL}/login`, payload)
  return RestResponse.handleDataResponse<LoginResult>(response)
}

export async function me() {
  const restApi = api<MeResult>()
  const response = await restApi.GET(`${BASE_URL}/me`)
  return RestResponse.handleDataResponse<MeResult>(response)
}

export async function changeContact(payload: ChangeContactPayload) {
  const restApi = api<OtpSendResult>()
  const response = await restApi.POST(`${BASE_URL}/change-contact`, payload)
  return RestResponse.handleDataResponse<OtpSendResult>(response)
}

export async function sendOtp(otpToken: string) {
  const restApi = api<OtpSendResult>()
  const response = await restApi.POST(`${BASE_URL}/otp/send`, { otpToken })
  return RestResponse.handleDataResponse<OtpSendResult>(response)
}

export async function verifyOtp(otpToken: string, code: string) {
  const restApi = api<OtpVerifyResult>()
  const response = await restApi.POST(`${BASE_URL}/otp/verify`, { otpToken, code })
  return RestResponse.handleDataResponse<OtpVerifyResult>(response)
}
