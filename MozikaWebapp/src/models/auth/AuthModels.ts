export interface RegisterPayload {
  email: string | null
  phone: string | null
  password: string
  role: 'CLIENT' | 'ARTIST'
  artistType: 'solo' | 'group' | null
  stageName: string | null

  // Champs optionnels du formulaire (transmis tels quels au backend)
  firstName?: string | null // CLIENT
  lastName?: string | null // CLIENT
  username?: string | null // CLIENT
  otpChannel?: 'EMAIL' | 'SMS' // défaut backend : EMAIL

}

export interface RegisterResult {
  id: number
  email: string
  phone: string
  role: string
  status: string

  otpToken: string // = String(userId) : le backend identifie l'OTP par userId
  expiresAt: string // ISO — le code OTP est valable 10 min côté backend

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

  // Le backend renvoie un JWT dès que l'OTP est valide
  token?: string
  userId?: number
  role?: string

}

export interface ChangeContactPayload {
  newEmail: string | null
  newPhone: string | null
}


// ─────────────────────────────────────────────────────────────────────────────
// Formes réelles renvoyées par le backend (usage interne à AuthService)
// ─────────────────────────────────────────────────────────────────────────────

export interface BackendRegisterResponse {
  userId: number
  email: string
  message: string
  status: string
}

export interface BackendAuthResponse {
  token: string
  userId: number
  email: string
  role: string
}

// GET /me renvoie ClientProfileResponse ou ArtistProfileResponse selon le compte
export interface BackendProfileResponse {
  userId: number
  email: string
  accountStatus: string
  stageName?: string | null // présent uniquement pour un ARTIST
}

