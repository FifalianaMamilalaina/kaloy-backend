<template>
  <div class="mz-page-view upload-page">
    <!-- Header standardisé -->
    <PageHeader
      title="Pipeline d'Ingestion"
      subtitle="Déposez une archive .zip contenant vos morceaux, vidéos, solfa et playbacks pour publication automatique"
    >
      <template #actions>
        <button
          type="button"
          @click="router.push('/contentSubmissions')"
          class="mz-btn mz-btn-secondary"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="8" y1="6" x2="21" y2="6"/>
            <line x1="8" y1="12" x2="21" y2="12"/>
            <line x1="8" y1="18" x2="21" y2="18"/>
            <line x1="3" y1="6" x2="3.01" y2="6"/>
            <line x1="3" y1="12" x2="3.01" y2="12"/>
            <line x1="3" y1="18" x2="3.01" y2="18"/>
          </svg>
          Suivi des soumissions
        </button>
      </template>
    </PageHeader>

    <div class="upload-grid">
      <!-- Colonne Gauche : Formulaire de Dépôt / Drag & Drop -->
      <div class="upload-main-col">
        <div class="mz-card upload-card">
          <div class="card-title-group">
            <div class="title-icon-badge">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17 8 12 3 7 8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
              </svg>
            </div>
            <div>
              <h2 class="card-main-title">Dépôt d'archive musicale</h2>
              <p class="card-subtitle">Format compressé .zip contenant la chanson et ses éléments associés</p>
            </div>
          </div>

          <!-- Zone de Glisser-Déposer (Dropzone) -->
          <div
            class="mz-dropzone"
            :class="{ 'is-dragover': isDragOver, 'has-file': Boolean(selectedFile) }"
            @dragover.prevent="onDragOver"
            @dragleave.prevent="onDragLeave"
            @drop.prevent="onDrop"
            @click="triggerFileInput"
          >
            <input
              ref="fileInputRef"
              type="file"
              accept=".zip,application/zip,application/x-zip-compressed"
              class="hidden-file-input"
              @change="onFileSelected"
            />

            <!-- État vide -->
            <div v-if="!selectedFile" class="dropzone-empty">
              <div class="dropzone-icon">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75">
                  <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
                  <line x1="12" y1="11" x2="12" y2="17"/>
                  <polyline points="9 14 12 11 15 14"/>
                </svg>
              </div>
              <div class="dropzone-prompt">
                <strong>Glissez-déposez votre archive .zip ici</strong>
                <span>ou cliquez pour parcourir vos dossiers</span>
              </div>
              <div class="dropzone-hint">
                Taille maximale : 200 Mo • Formats acceptés : .zip
              </div>
            </div>

            <!-- Fichier sélectionné -->
            <div v-else class="dropzone-file" @click.stop>
              <div class="file-icon-wrap">
                <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="10" y1="12" x2="14" y2="12"/>
                  <line x1="10" y1="15" x2="14" y2="15"/>
                  <line x1="10" y1="18" x2="12" y2="18"/>
                </svg>
              </div>
              <div class="file-details">
                <div class="file-name" :title="selectedFile.name">{{ selectedFile.name }}</div>
                <div class="file-meta">
                  <span class="mz-badge mz-badge-accent">Archive ZIP</span>
                  <span class="file-size">{{ formatFileSize(selectedFile.size) }}</span>
                </div>
              </div>
              <button
                type="button"
                class="remove-file-btn"
                title="Supprimer le fichier"
                @click="removeFile"
              >
                ✕
              </button>
            </div>
          </div>

          <!-- Champs d'aide au nommage pour le pipeline -->
          <div class="form-grid">
            <div class="form-group">
              <label class="form-label" for="artistNameInput">
                Nom de scène de l'artiste <span class="required">*</span>
              </label>
              <input
                id="artistNameInput"
                v-model="artistName"
                type="text"
                class="mz-input"
                placeholder="Ex : Rossy, Mage 4, Vetsonkira..."
                @input="updateExpectedArchiveName"
              />
              <span class="field-hint">Le nom exact de l'artiste dans le catalogue Kaloy</span>
            </div>

            <div class="form-group">
              <label class="form-label" for="songTitleInput">
                Titre du morceau ou de l'album
              </label>
              <input
                id="songTitleInput"
                v-model="songTitle"
                type="text"
                class="mz-input"
                placeholder="Ex : Tsy Very, Eh sambatra sy tretrika..."
                @input="updateExpectedArchiveName"
              />
              <span class="field-hint">Nom du titre ou de l'album importé</span>
            </div>
          </div>

          <!-- Aperçu du nom dans le pipeline -->
          <div class="pipeline-preview-card" v-if="computedArchiveName">
            <span class="preview-label">Nom dans le pipeline :</span>
            <code class="preview-code">{{ computedArchiveName }}</code>
            <span
              v-if="isValidNaming"
              class="mz-badge mz-badge-success preview-badge"
            >
              ✓ Conforme
            </span>
            <span
              v-else
              class="mz-badge mz-badge-warning preview-badge"
            >
              ⚠️ Renseignez l'artiste
            </span>
          </div>

          <!-- Barre de progression -->
          <div v-if="isUploading" class="upload-progress-container">
            <div class="progress-labels">
              <span>Téléversement vers le pipeline...</span>
              <strong>{{ uploadProgress }}%</strong>
            </div>
            <div class="progress-bar-bg">
              <div class="progress-bar-fill" :style="{ width: uploadProgress + '%' }"></div>
            </div>
          </div>

          <!-- Alertes de statut -->
          <div v-if="successMessage" class="alert-box alert-success">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <div>
              <strong>Succès !</strong>
              <p>{{ successMessage }}</p>
            </div>
          </div>

          <div v-if="errorMessage" class="alert-box alert-danger">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <div>
              <strong>Erreur</strong>
              <p>{{ errorMessage }}</p>
            </div>
          </div>

          <!-- Actions -->
          <div class="upload-actions">
            <button
              type="button"
              class="mz-btn mz-btn-secondary"
              @click="resetForm"
              :disabled="isUploading"
            >
              Réinitialiser
            </button>
            <button
              type="button"
              class="mz-btn mz-btn-primary mz-btn-lg"
              :disabled="!canSubmit || isUploading"
              @click="handleSubmit"
            >
              <svg v-if="!isUploading" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17 8 12 3 7 8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
              </svg>
              <span v-if="isUploading" class="spinner-sm"></span>
              <span>{{ isUploading ? 'Téléversement en cours...' : 'Envoyer au pipeline' }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Colonne Droite : Guide & Spécifications du Pipeline -->
      <div class="upload-sidebar-col">
        <!-- Carte Guide du Zip -->
        <div class="mz-card guide-card">
          <h3 class="guide-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="16" x2="12" y2="12"/>
              <line x1="12" y1="8" x2="12.01" y2="8"/>
            </svg>
            Contenu attendu de l'archive
          </h3>
          <p class="guide-intro">
            Placez vos fichiers directement à la racine du fichier <code>.zip</code> :
          </p>

          <ul class="files-breakdown">
            <li class="file-type-item">
              <span class="file-type-badge audio-badge">MP3</span>
              <div class="file-type-info">
                <strong>Audio principal (Chanson)</strong>
                <span>Obligatoire. Piste audio publiée dans le catalogue.</span>
              </div>
            </li>

            <li class="file-type-item">
              <span class="file-type-badge video-badge">MP4</span>
              <div class="file-type-info">
                <strong>Clip Vidéo</strong>
                <span>Facultatif. Clip musical associé automatiquement au titre.</span>
              </div>
            </li>

            <li class="file-type-item">
              <span class="file-type-badge pdf-badge">PDF</span>
              <div class="file-type-info">
                <strong>Partition Solfa / Paroles</strong>
                <span>Facultatif. Nommé <code>Nom_Titre_paroles.pdf</code> ou partition solfa.</span>
              </div>
            </li>

            <li class="file-type-item">
              <span class="file-type-badge zip-badge">ZIP/M4A</span>
              <div class="file-type-info">
                <strong>Playback / Instrumental</strong>
                <span>Facultatif. Piste ou archive d'accompagnement playback.</span>
              </div>
            </li>

            <li class="file-type-item">
              <span class="file-type-badge xlsx-badge">XLSX</span>
              <div class="file-type-info">
                <strong>Tableur de Métadonnées</strong>
                <span>Facultatif. Renseigne titre, album, genre, date de sortie.</span>
              </div>
            </li>
          </ul>

          <div class="guide-footer-callout">
            <strong>⚙️ Fonctionnement automatique</strong>
            <p>
              Le pipeline extrait l'archive, téléverse les médias, rattache l'artiste, crée l'album et la chanson en base, puis supprime l'archive une fois le succès validé.
            </p>
          </div>
        </div>

        <!-- Convention de nommage -->
        <div class="mz-card guide-naming-card">
          <h4 class="naming-title">Règle de nommage du fichier</h4>
          <div class="naming-rule-box">
            <span class="rule-part artist-part">NomArtiste</span>
            <span class="rule-separator">_</span>
            <span class="rule-part title-part">TitreMorceau</span>
            <span class="rule-ext">.zip</span>
          </div>
          <p class="naming-tip">
            Tout ce qui précède le premier tiret bas <code>_</code> identifie l'artiste dans Kaloy. Les accents et majuscules sont tolérés.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/common/PageHeader.vue'
import { uploadArchiveToPipeline } from '@/services/UploadService'

const router = useRouter()

const fileInputRef = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const artistName = ref('')
const songTitle = ref('')

const isDragOver = ref(false)
const isUploading = ref(false)
const uploadProgress = ref(0)
const successMessage = ref('')
const errorMessage = ref('')

// Détection et calcul du nom d'archive
const computedArchiveName = computed(() => {
  if (artistName.value.trim() && songTitle.value.trim()) {
    return `${artistName.value.trim()}_${songTitle.value.trim()}.zip`
  }
  if (artistName.value.trim() && selectedFile.value) {
    const rawName = selectedFile.value.name.replace(/\.zip$/i, '')
    const parts = rawName.split('_')
    const subPart = parts.length > 1 ? parts.slice(1).join('_') : rawName
    return `${artistName.value.trim()}_${subPart || 'Morceau'}.zip`
  }
  if (selectedFile.value) {
    return selectedFile.value.name
  }
  return ''
})

const isValidNaming = computed(() => {
  const name = computedArchiveName.value
  return Boolean(name && name.includes('_') && name.toLowerCase().endsWith('.zip'))
})

const canSubmit = computed(() => {
  return Boolean(selectedFile.value && isValidNaming.value && !isUploading.value)
})

// Gestion du fichier
const triggerFileInput = () => {
  if (!isUploading.value) {
    fileInputRef.value?.click()
  }
}

const onFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    handleFile(target.files[0])
  }
}

const onDragOver = () => {
  isDragOver.value = true
}

const onDragLeave = () => {
  isDragOver.value = false
}

const onDrop = (event: DragEvent) => {
  isDragOver.value = false
  if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
    handleFile(event.dataTransfer.files[0])
  }
}

const handleFile = (file: File) => {
  if (!file.name.toLowerCase().endsWith('.zip')) {
    errorMessage.value = 'Veuillez sélectionner un fichier au format .zip valide.'
    return
  }
  selectedFile.value = file
  errorMessage.value = ''
  successMessage.value = ''

  // Tentative d'auto-remplissage artiste / titre depuis le nom de fichier
  const baseName = file.name.replace(/\.zip$/i, '')
  if (baseName.includes('_')) {
    const parts = baseName.split('_')
    if (!artistName.value) artistName.value = parts[0]
    if (!songTitle.value && parts[1]) songTitle.value = parts.slice(1).join('_')
  } else {
    // Si le fichier contient un tiret ou espace, déduire artiste ou titre
    if (!artistName.value && baseName.toLowerCase().includes('ampifitia')) {
      artistName.value = 'Ampifitia'
    }
    if (!songTitle.value) {
      let cleanTitle = baseName
      if (artistName.value) {
        cleanTitle = cleanTitle.replace(new RegExp(artistName.value, 'gi'), '').replace(/^[-_\s]+|[-_\s]+$/g, '')
      }
      songTitle.value = cleanTitle || baseName
    }
  }
}

const removeFile = () => {
  selectedFile.value = null
  if (fileInputRef.value) fileInputRef.value.value = ''
}

const updateExpectedArchiveName = () => {
  errorMessage.value = ''
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 o'
  const k = 1024
  const sizes = ['o', 'Ko', 'Mo', 'Go']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const resetForm = () => {
  removeFile()
  artistName.value = ''
  songTitle.value = ''
  errorMessage.value = ''
  successMessage.value = ''
  uploadProgress.value = 0
}

// Soumission vers le backend
const handleSubmit = async () => {
  if (!selectedFile.value || !isValidNaming.value) return

  isUploading.value = true
  uploadProgress.value = 0
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const res = await uploadArchiveToPipeline(
      selectedFile.value,
      artistName.value.trim(),
      songTitle.value.trim(),
      computedArchiveName.value,
      (pct) => {
        uploadProgress.value = pct
      }
    )

    successMessage.value = res.message || `Archive ${computedArchiveName.value} transmise au pipeline d'ingestion avec succès !`
    setTimeout(() => {
      router.push('/contentSubmissions')
    }, 2500)
  } catch (err: any) {
    console.error('Erreur téléversement pipeline:', err)
    errorMessage.value =
      err.response?.data?.message ||
      err.message ||
      'Échec du téléversement de l’archive vers le pipeline. Vérifiez que le backend Kaloy est actif.'
  } finally {
    isUploading.value = false
  }
}
</script>

<style scoped>
.upload-page {
  max-width: 1140px;
  margin: 0 auto;
}

.upload-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: var(--mz-space-6);
  align-items: start;
}

@media (max-width: 992px) {
  .upload-grid {
    grid-template-columns: 1fr;
  }
}

/* ── Carte principale ── */
.upload-card {
  padding: var(--mz-space-6);
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-5);
}

.card-title-group {
  display: flex;
  align-items: center;
  gap: var(--mz-space-4);
}

.title-icon-badge {
  width: 48px;
  height: 48px;
  border-radius: var(--mz-radius-md);
  background: linear-gradient(135deg, var(--mz-color-primary) 0%, var(--mz-color-secondary) 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
}

.card-main-title {
  font-size: var(--mz-font-size-lg);
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  margin: 0;
}

.card-subtitle {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  margin: 2px 0 0;
}

/* ── Dropzone ── */
.mz-dropzone {
  border: 2px dashed var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-lg);
  background: #fbfbfe;
  padding: var(--mz-space-8) var(--mz-space-5);
  cursor: pointer;
  transition: all var(--mz-transition-base);
  position: relative;
  overflow: hidden;
}

.mz-dropzone:hover {
  border-color: var(--mz-color-primary);
  background: rgba(139, 92, 246, 0.02);
}

.mz-dropzone.is-dragover {
  border-color: var(--mz-color-primary);
  background: rgba(139, 92, 246, 0.08);
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.15);
}

.mz-dropzone.has-file {
  background: #ffffff;
  border-style: solid;
  border-color: #e2e8f0;
  padding: var(--mz-space-4);
}

.hidden-file-input {
  display: none;
}

.dropzone-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--mz-space-3);
  text-align: center;
}

.dropzone-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--mz-radius-full);
  background: rgba(139, 92, 246, 0.08);
  color: var(--mz-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.dropzone-prompt strong {
  display: block;
  font-size: var(--mz-font-size-base);
  color: var(--mz-text-on-light-high);
  margin-bottom: 2px;
}

.dropzone-prompt span {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
}

.dropzone-hint {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 99px;
  margin-top: var(--mz-space-2);
}

/* ── Fichier sélectionné dans dropzone ── */
.dropzone-file {
  display: flex;
  align-items: center;
  gap: var(--mz-space-4);
}

.file-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: var(--mz-radius-md);
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(245, 158, 11, 0.25);
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: var(--mz-font-size-base);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  margin-top: 4px;
}

.file-size {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
}

.remove-file-btn {
  width: 32px;
  height: 32px;
  border-radius: var(--mz-radius-full);
  border: 1px solid #e2e8f0;
  background: #ffffff;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--mz-transition-base);
}

.remove-file-btn:hover {
  background: #fee2e2;
  color: #ef4444;
  border-color: #fecaca;
}

/* ── Formulaire d'aide ── */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--mz-space-4);
}

@media (max-width: 600px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-label {
  font-size: 12px;
  font-weight: 700;
  color: var(--mz-text-on-light-high);
}

.required {
  color: #ef4444;
}

.mz-input {
  width: 100%;
  padding: 10px 14px;
  border-radius: var(--mz-radius-md);
  border: 1px solid var(--mz-border-subtle-light);
  background: #ffffff;
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-high);
  transition: border-color var(--mz-transition-base);
}

.mz-input:focus {
  outline: none;
  border-color: var(--mz-color-primary);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.12);
}

.field-hint {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
}

/* ── Prévisualisation nom ── */
.pipeline-preview-card {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding: var(--mz-space-3) var(--mz-space-4);
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: var(--mz-radius-md);
  flex-wrap: wrap;
}

.preview-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--mz-text-on-light-muted);
}

.preview-code {
  font-family: monospace;
  font-weight: 700;
  font-size: 13px;
  color: var(--mz-color-primary);
}

.preview-badge {
  margin-left: auto;
}

/* ── Barre de progression ── */
.upload-progress-container {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--mz-text-on-light-muted);
}

.progress-bar-bg {
  width: 100%;
  height: 8px;
  background: #e2e8f0;
  border-radius: 99px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--mz-color-primary), var(--mz-color-secondary));
  transition: width 0.2s ease;
}

/* ── Alertes ── */
.alert-box {
  display: flex;
  align-items: flex-start;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
  border-radius: var(--mz-radius-md);
  font-size: var(--mz-font-size-sm);
}

.alert-box p {
  margin: 2px 0 0;
  font-size: 12px;
}

.alert-success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.alert-danger {
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

/* ── Actions ── */
.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--mz-space-3);
  padding-top: var(--mz-space-4);
  border-top: 1px solid var(--mz-border-subtle-light);
}

.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Sidebar Guide ── */
.guide-card {
  padding: var(--mz-space-5);
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-4);
}

.guide-title {
  font-size: var(--mz-font-size-base);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
}

.guide-intro {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  margin: 0;
}

.guide-intro code {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
}

.files-breakdown {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
}

.file-type-item {
  display: flex;
  align-items: flex-start;
  gap: var(--mz-space-3);
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.file-type-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.file-type-badge {
  font-size: 10px;
  font-weight: 800;
  padding: 3px 8px;
  border-radius: 6px;
  letter-spacing: 0.05em;
  color: #ffffff;
  flex-shrink: 0;
}

.audio-badge { background: #8b5cf6; }
.video-badge { background: #ef4444; }
.pdf-badge { background: #3b82f6; }
.zip-badge { background: #10b981; }
.xlsx-badge { background: #14b8a6; }

.file-type-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-type-info strong {
  font-size: 12px;
  color: var(--mz-text-on-light-high);
}

.file-type-info span {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
}

.file-type-info code {
  font-size: 10px;
  background: #f1f5f9;
  padding: 1px 4px;
  border-radius: 3px;
}

.guide-footer-callout {
  padding: var(--mz-space-3) var(--mz-space-4);
  background: rgba(139, 92, 246, 0.06);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: var(--mz-radius-md);
  font-size: 11px;
}

.guide-footer-callout strong {
  color: var(--mz-color-primary);
  display: block;
  margin-bottom: 2px;
}

.guide-footer-callout p {
  margin: 0;
  color: var(--mz-text-on-light-muted);
}

/* ── Naming Card ── */
.guide-naming-card {
  padding: var(--mz-space-5);
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
}

.naming-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
}

.naming-rule-box {
  display: flex;
  align-items: center;
  gap: 2px;
  background: #1e1b4b;
  padding: 8px 12px;
  border-radius: var(--mz-radius-md);
  font-family: monospace;
  font-size: 13px;
  font-weight: 700;
}

.artist-part { color: #a78bfa; }
.rule-separator { color: #f59e0b; }
.title-part { color: #6ee7b7; }
.rule-ext { color: #94a3b8; }

.naming-tip {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
  margin: 0;
  line-height: 1.4;
}
</style>
