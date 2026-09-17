import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'
import { Album } from './AlbumModel'
import * as albumService from '@/services/AlbumService'
import { AudioStorageType } from './AudioStorageTypeModel'
import * as audioStorageTypeService from '@/services/AudioStorageTypeService'

export interface ISong {
  id?: number
  artistidArtists?: Artist
  albumidAlbums?: Album
  title?: string
  durationSeconds?: number
  releaseDate?: string
  language?: string
  authorComposer?: string
  musicalArranger?: string
  recordingLocation?: string
  recordingDate?: string
  storagetypeidAudioStorageTypes?: AudioStorageType
  audioUrl?: string
  audioFile?: Uint8Array
  videoUrl?: string
  karaokeAudio?: Uint8Array
  lyrics?: string
  lyricsSyncData?: any
  solfa?: string
  playback?: Uint8Array
  isDownloadable?: boolean
  createdAt?: Date
}

export class Song extends BaseModel implements ISong {
  id?: number
  artistidArtists?: Artist
  albumidAlbums?: Album
  title?: string
  durationSeconds?: number
  releaseDate?: string
  language?: string
  authorComposer?: string
  musicalArranger?: string
  recordingLocation?: string
  recordingDate?: string
  storagetypeidAudioStorageTypes?: AudioStorageType
  audioUrl?: string
  audioFile?: Uint8Array
  videoUrl?: string
  karaokeAudio?: Uint8Array
  lyrics?: string
  lyricsSyncData?: any
  solfa?: string
  playback?: Uint8Array
  isDownloadable?: boolean
  createdAt?: Date

  constructor(data?: Partial<ISong>) {
    super()
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.albumidAlbums = data?.albumidAlbums ? new Album(data?.albumidAlbums) : undefined
    this.title = data?.title
    this.durationSeconds = data?.durationSeconds
    this.releaseDate = data?.releaseDate
    this.language = data?.language
    this.authorComposer = data?.authorComposer
    this.musicalArranger = data?.musicalArranger
    this.recordingLocation = data?.recordingLocation
    this.recordingDate = data?.recordingDate
    this.storagetypeidAudioStorageTypes = data?.storagetypeidAudioStorageTypes
      ? new AudioStorageType(data?.storagetypeidAudioStorageTypes)
      : undefined
    this.audioUrl = data?.audioUrl
    this.audioFile = data?.audioFile
    this.videoUrl = data?.videoUrl
    this.karaokeAudio = data?.karaokeAudio
    this.lyrics = data?.lyrics
    this.lyricsSyncData = data?.lyricsSyncData
    this.solfa = data?.solfa
    this.playback = data?.playback
    this.isDownloadable = data?.isDownloadable
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'title'
  }

  public override getReferenceValue(): string {
    return String(this.title)
  }

  protected static override searchDaoMetadata: EntitySearchField[] = [
    {
      key: 'id',
      label: 'Id',
      type: 'string',
      sortable: true,
      showInTable: true,
      showInFilter: true,
      identifier: true,
    },

    {
      key: 'artistidArtists',
      label: 'Artist',
      type: 'select',
      searchKey: Artist.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Artist, artistService),
      multicriteriaSelect: {
        filters: Artist.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(artistService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'albumidAlbums',
      label: 'Album',
      type: 'select',
      searchKey: Album.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Album, albumService),
      multicriteriaSelect: {
        filters: Album.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(albumService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'title',
      label: 'Title',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'durationSeconds',
      label: 'Duration seconds',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'durationSecondsMin',
      label: 'Duration seconds Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'durationSecondsMax',
      label: 'Duration seconds Max',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'releaseDate',
      label: 'Release date',
      type: 'date',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'releaseDateMin',
      label: 'Release date Min',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'releaseDateMax',
      label: 'Release date Max',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'language',
      label: 'Language',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'authorComposer',
      label: 'Author composer',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'musicalArranger',
      label: 'Musical arranger',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'recordingLocation',
      label: 'Recording location',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'recordingDate',
      label: 'Recording date',
      type: 'date',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'recordingDateMin',
      label: 'Recording date Min',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'recordingDateMax',
      label: 'Recording date Max',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'storagetypeidAudioStorageTypes',
      label: 'Audio storage type',
      type: 'select',
      searchKey: AudioStorageType.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(AudioStorageType, audioStorageTypeService),
      multicriteriaSelect: {
        filters: AudioStorageType.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(audioStorageTypeService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'audioUrl',
      label: 'Audio url',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'audioFile',
      label: 'Audio file',
      type: 'file',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'videoUrl',
      label: 'Video url',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'karaokeAudio',
      label: 'Karaoke audio',
      type: 'file',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'lyrics',
      label: 'Lyrics',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'lyricsSyncData',
      label: 'Lyrics sync data',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'solfa',
      label: 'Solfa',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'playback',
      label: 'Playback',
      type: 'file',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'isDownloadable',
      label: 'Is downloadable',
      type: 'checkbox',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class SongFormDTO {
  id?: number
  artistidArtists?: string

  albumidAlbums?: string

  title?: string
  durationSeconds?: number
  releaseDate?: string
  language?: string
  authorComposer?: string
  musicalArranger?: string
  recordingLocation?: string
  recordingDate?: string
  storagetypeidAudioStorageTypes?: string

  audioUrl?: string
  audioFile?: Uint8Array
  videoUrl?: string
  karaokeAudio?: Uint8Array
  lyrics?: string
  lyricsSyncData?: any
  solfa?: string
  playback?: Uint8Array
  isDownloadable?: boolean
  createdAt?: Date

  constructor(data?: Partial<SongFormDTO>) {
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ?? ''

    this.albumidAlbums = data?.albumidAlbums ?? ''

    this.title = data?.title
    this.durationSeconds = data?.durationSeconds
    this.releaseDate = data?.releaseDate ? data.releaseDate : undefined

    this.language = data?.language
    this.authorComposer = data?.authorComposer
    this.musicalArranger = data?.musicalArranger
    this.recordingLocation = data?.recordingLocation
    this.recordingDate = data?.recordingDate ? data.recordingDate : undefined

    this.storagetypeidAudioStorageTypes = data?.storagetypeidAudioStorageTypes ?? ''

    this.audioUrl = data?.audioUrl
    this.audioFile = data?.audioFile
    this.videoUrl = data?.videoUrl
    this.karaokeAudio = data?.karaokeAudio
    this.lyrics = data?.lyrics
    this.lyricsSyncData = data?.lyricsSyncData
    this.solfa = data?.solfa
    this.playback = data?.playback
    this.isDownloadable = data?.isDownloadable
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<ISong> | ISong | null) {
    const instance = new SongFormDTO()
    instance.id = data?.id
    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.albumidAlbums = data?.albumidAlbums?.getKeyValue()?.toString()

    instance.title = data?.title
    instance.durationSeconds = data?.durationSeconds
    instance.releaseDate = data?.releaseDate
    instance.language = data?.language
    instance.authorComposer = data?.authorComposer
    instance.musicalArranger = data?.musicalArranger
    instance.recordingLocation = data?.recordingLocation
    instance.recordingDate = data?.recordingDate
    instance.storagetypeidAudioStorageTypes = data?.storagetypeidAudioStorageTypes
      ?.getKeyValue()
      ?.toString()

    instance.audioUrl = data?.audioUrl
    instance.audioFile = data?.audioFile
    instance.videoUrl = data?.videoUrl
    instance.karaokeAudio = data?.karaokeAudio
    instance.lyrics = data?.lyrics
    instance.lyricsSyncData = data?.lyricsSyncData
    instance.solfa = data?.solfa
    instance.playback = data?.playback
    instance.isDownloadable = data?.isDownloadable
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Song[] | null): SongFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => SongFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Song>> {
    const entity: Partial<Song> = new Song({
      id: this.id,

      title: this.title,

      durationSeconds: this.durationSeconds,

      releaseDate: this.releaseDate ? this.releaseDate.split('T')[0] : undefined,

      language: this.language,

      authorComposer: this.authorComposer,

      musicalArranger: this.musicalArranger,

      recordingLocation: this.recordingLocation,

      recordingDate: this.recordingDate ? this.recordingDate.split('T')[0] : undefined,

      audioUrl: this.audioUrl,

      audioFile: this.audioFile,

      videoUrl: this.videoUrl,

      karaokeAudio: this.karaokeAudio,

      lyrics: this.lyrics,

      lyricsSyncData: this.lyricsSyncData,

      solfa: this.solfa,

      playback: this.playback,

      isDownloadable: this.isDownloadable,

      createdAt: this.createdAt,
    })

    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    if (this.albumidAlbums && this.albumidAlbums.length > 0) {
      const related = await albumService.getById(Number(this.albumidAlbums))
      entity.albumidAlbums = related.data as Album
    }
    if (this.storagetypeidAudioStorageTypes && this.storagetypeidAudioStorageTypes.length > 0) {
      const related = await audioStorageTypeService.getById(
        Number(this.storagetypeidAudioStorageTypes),
      )
      entity.storagetypeidAudioStorageTypes = related.data as AudioStorageType
    }
    return entity
  }

  static async toEntities(data?: SongFormDTO[] | null) {
    const entities: Promise<Partial<Song>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
