import axios, { type AxiosProgressEvent } from 'axios'
import RestResponse from '@/models/api/RestResponseModel'

const API_BASE_URL = import.meta.env.VITE_APP_API_URL || 'http://localhost:8087/mozika'

export interface UploadArchiveResult {
  nomArchive: string
  taille: number
  statut: string
  destination?: string
}

/**
 * Service pour téléverser des archives vers le pipeline d'ingestion
 * et des fichiers médias unitaires vers l'API Mozika.
 */
export async function uploadArchiveToPipeline(
  file: File,
  artistName?: string,
  songTitle?: string,
  nomArchive?: string,
  onProgress?: (percent: number) => void
): Promise<RestResponse<UploadArchiveResult>> {
  const formData = new FormData()
  formData.append('fichier', file)
  if (artistName) formData.append('artiste', artistName)
  if (songTitle) formData.append('titre', songTitle)
  if (nomArchive) formData.append('nomArchive', nomArchive)

  const token = localStorage.getItem('token')
  const headers: Record<string, string> = {}
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await axios.post(`${API_BASE_URL}/uploads/archive`, formData, {
    headers,
    onUploadProgress: (progressEvent: AxiosProgressEvent) => {
      if (progressEvent.total && onProgress) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(percent)
      }
    },
  })

  return RestResponse.fromJson<UploadArchiveResult>(response.data)
}

/**
 * Téléversement unitaire d'un fichier média (audio mp3, vidéo mp4, partition pdf, playback zip/audio)
 */
export async function uploadMediaFile(
  file: File,
  onProgress?: (percent: number) => void
): Promise<string> {
  const formData = new FormData()
  formData.append('fichier', file)

  const token = localStorage.getItem('token')
  const headers: Record<string, string> = {}
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await axios.post(`${API_BASE_URL}/uploads`, formData, {
    headers,
    onUploadProgress: (progressEvent: AxiosProgressEvent) => {
      if (progressEvent.total && onProgress) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(percent)
      }
    },
  })

  const rest = RestResponse.fromJson<string>(response.data)
  return rest.data ?? ''
}
