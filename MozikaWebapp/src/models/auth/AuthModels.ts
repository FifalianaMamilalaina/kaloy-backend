export interface RegisterPayload {
  email: string | null
  phone: string | null
  password: string
  role: 'CLIENT' | 'ARTIST'
  artistType: 'solo' | 'group' | null
  stageName: string | null
}

export interface RegisterResult {
  id: number
  email: string
  phone: string
  role: string
  status: string
  otpToken: string
}

export interface LoginPayload {
  email: string | null
  phone: string | null
  password: string
}

export interface LoginResult {
  token: string
  userId: number
  role: string
}

export interface MeResult {
  id: number
  email: string
  role: string
  status: string
}

export interface OtpSendResult {
  message: string
  destination: string
  expiresAt: string
  otpToken: string
}

export interface OtpVerifyResult {
  message: string
  status: string
}

export interface ChangeContactPayload {
  newEmail: string | null
  newPhone: string | null
}
