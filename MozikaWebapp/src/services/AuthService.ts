import api from '@/services/api'
import RestResponse from '@/models/api/RestResponseModel'
import type { DataResponse } from '@/models/api/DataResponseModel'

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

  BackendRegisterResponse,
  BackendAuthResponse,
  BackendProfileResponse,
} from '@/models/auth/AuthModels'

// VITE_APP_API_URL contient déjà le context-path du backend (/mozika).
// Les routes backend sont donc /auth/... et /me/... (pas de préfixe /api).
const AUTH_URL = '/auth'
const ME_URL = '/me'

// Alignés sur le backend (DefaultAuthService) : OTP valable 10 min,
// 60 s minimum entre deux envois.
const OTP_TTL_MS = 10 * 60 * 1000
const RESEND_COOLDOWN_MS = 60 * 1000

// Le backend identifie un OTP par userId. Les vues, elles, manipulent un
// "otpToken" : on utilise String(userId) comme token, et on garde en mémoire
// à qui / quand le dernier code a été envoyé.
const otpStates = new Map<string, { destination: string; sentAt: number }>()

function isoIn(fromMs: number): string {
  return new Date(fromMs + OTP_TTL_MS).toISOString()
}

function fail<T>(message: string, status = 400): DataResponse<T> {
  return { success: false, status, data: undefined, error: message, errors: null }
}

function mapData<A, B>(res: DataResponse<A>, fn: (data: A) => B): DataResponse<B> {
  return { ...res, data: res.success ? fn(res.data as A) : undefined }
}

// Retire les clés null/undefined pour laisser jouer les valeurs par défaut du backend
function compact(obj: Record<string, unknown>): Record<string, unknown> {
  return Object.fromEntries(Object.entries(obj).filter(([, v]) => v !== null && v !== undefined))
}

// ── POST /auth/register/client | /auth/register/artist ───────────────────────
export async function register(payload: RegisterPayload): Promise<DataResponse<RegisterResult>> {
  const isArtist = payload.role === 'ARTIST'

  const body = isArtist
    ? compact({
        email: payload.email,
        password: payload.password,
        phone: payload.phone,
        artistType: payload.artistType?.toUpperCase(),
        stageName: payload.stageName,
        otpChannel: payload.otpChannel,
      })
    : compact({
        email: payload.email,
        password: payload.password,
        phone: payload.phone,
        firstName: payload.firstName,
        lastName: payload.lastName,
        username: payload.username,
        otpChannel: payload.otpChannel,
      })

  const restApi = api<BackendRegisterResponse>()
  const response = await restApi.POST(
    `${AUTH_URL}/register/${isArtist ? 'artist' : 'client'}`,
    body,
  )

  return mapData(RestResponse.handleDataResponse<BackendRegisterResponse>(response), (d) => {
    const otpToken = String(d.userId)
    const sentAt = Date.now()
    otpStates.set(otpToken, { destination: d.email, sentAt })
    return {
      id: d.userId,
      email: d.email,
      phone: payload.phone ?? '',
      role: payload.role,
      status: d.status,
      otpToken,
      expiresAt: isoIn(sentAt),
    }
  })
}

// ── POST /auth/login ─────────────────────────────────────────────────────────
export async function login(payload: LoginPayload): Promise<DataResponse<LoginResult>> {
  const restApi = api<BackendAuthResponse>()
  const response = await restApi.POST(`${AUTH_URL}/login`, {
    email: payload.email,
    password: payload.password,
  })

  return mapData(RestResponse.handleDataResponse<BackendAuthResponse>(response), (d) => ({
    token: d.token,
    userId: d.userId,
    role: d.role,
  }))
}

// ── GET /me ──────────────────────────────────────────────────────────────────
export async function me(): Promise<DataResponse<MeResult>> {
  const restApi = api<BackendProfileResponse>()
  const response = await restApi.GET(ME_URL)

  return mapData(RestResponse.handleDataResponse<BackendProfileResponse>(response), (p) => ({
    id: p.userId,
    email: p.email,
    role: p.stageName !== undefined ? 'ARTIST' : 'CLIENT',
    status: p.accountStatus,
  }))
}

// ── POST /me/email | /me/phone (1re étape : envoi du code) ───────────────────
// ⚠ La confirmation (/me/email/confirm, /me/phone/confirm) n'est pas encore
//   branchée côté frontend : voir le garde-fou dans verifyOtp().
export async function changeContact(
  payload: ChangeContactPayload,
): Promise<DataResponse<OtpSendResult>> {
  const isEmail = !!payload.newEmail
  if (!isEmail && !payload.newPhone) return fail('Aucun nouveau contact fourni.')

  const restApi = api<null>()
  const response = isEmail
    ? await restApi.POST(`${ME_URL}/email`, { newEmail: payload.newEmail })
    : await restApi.POST(`${ME_URL}/phone`, { newPhone: payload.newPhone })

  return mapData(RestResponse.handleDataResponse<null>(response), () => ({
    message: response.message,
    destination: (isEmail ? payload.newEmail : payload.newPhone) as string,
    expiresAt: isoIn(Date.now()),
    otpToken: '',
  }))
}

// ── POST /auth/resend-otp ────────────────────────────────────────────────────
export async function sendOtp(otpToken: string): Promise<DataResponse<OtpSendResult>> {
  const userId = Number(otpToken)
  if (!otpToken || Number.isNaN(userId)) {
    return fail(
      "Aucune inscription en cours pour ce code (la confirmation d'un changement de contact n'est pas encore branchée).",
    )
  }

  // Un code vient d'être envoyé (à l'inscription ou par un renvoi) : le backend
  // refuserait un renvoi avant 60 s ("Attendez 60 secondes"), donc on ne l'appelle pas.
  const last = otpStates.get(otpToken)
  if (last && Date.now() - last.sentAt < RESEND_COOLDOWN_MS) {
    return {
      success: true,
      status: 200,
      errors: null,
      data: {
        message: 'Un code vient déjà d’être envoyé',
        destination: last.destination,
        expiresAt: isoIn(last.sentAt),
        otpToken,
      },
    }
  }

  const restApi = api<string | null>()
  const response = await restApi.POST(`${AUTH_URL}/resend-otp`, { userId })

  return mapData(RestResponse.handleDataResponse<string | null>(response), () => {
    const sentAt = Date.now()
    const destination = last?.destination ?? 'votre email'
    otpStates.set(otpToken, { destination, sentAt })
    return { message: response.message, destination, expiresAt: isoIn(sentAt), otpToken }
  })
}

// ── POST /auth/verify-otp ────────────────────────────────────────────────────
export async function verifyOtp(
  otpToken: string,
  code: string,
): Promise<DataResponse<OtpVerifyResult>> {
  const userId = Number(otpToken)
  if (!otpToken || Number.isNaN(userId)) {
    return fail(
      "Aucune inscription en cours pour ce code (la confirmation d'un changement de contact n'est pas encore branchée).",
    )
  }

  const restApi = api<BackendAuthResponse>()
  const response = await restApi.POST(`${AUTH_URL}/verify-otp`, { userId, code })

  return mapData(RestResponse.handleDataResponse<BackendAuthResponse>(response), (d) => ({
    message: response.message,
    status: 'VERIFIED',
    token: d.token,
    userId: d.userId,
    role: d.role,
  }))
}
