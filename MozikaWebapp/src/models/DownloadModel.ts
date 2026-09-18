import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { Playlist } from './PlaylistModel'
import * as playlistService from '@/services/PlaylistService'

export interface IDownload {
  id?: number
  useridUsers?: User
  playlistidPlaylists?: Playlist
  downloadedAt?: Date
  expiresAt?: Date
}

export class Download extends BaseModel implements IDownload {
  id?: number
  useridUsers?: User
  playlistidPlaylists?: Playlist
  downloadedAt?: Date
  expiresAt?: Date

  constructor(data?: Partial<IDownload>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.playlistidPlaylists = data?.playlistidPlaylists
      ? new Playlist(data?.playlistidPlaylists)
      : undefined
    this.downloadedAt = data?.downloadedAt
    this.expiresAt = data?.expiresAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'downloadedAt'
  }

  public override getReferenceValue(): string {
    return String(this.downloadedAt)
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
      key: 'useridUsers',
      label: 'User',
      type: 'select',
      searchKey: User.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(User, userService),
      multicriteriaSelect: {
        filters: User.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(userService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'playlistidPlaylists',
      label: 'Playlist',
      type: 'select',
      searchKey: Playlist.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Playlist, playlistService),
      multicriteriaSelect: {
        filters: Playlist.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(playlistService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'downloadedAt',
      label: 'Downloaded at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'expiresAt',
      label: 'Expires at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class DownloadFormDTO {
  id?: number
  useridUsers?: string

  playlistidPlaylists?: string

  downloadedAt?: Date
  expiresAt?: Date

  constructor(data?: Partial<DownloadFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.playlistidPlaylists = data?.playlistidPlaylists ?? ''

    this.downloadedAt = data?.downloadedAt
    this.expiresAt = data?.expiresAt
  }

  static parse(data?: Partial<IDownload> | IDownload | null) {
    const instance = new DownloadFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.playlistidPlaylists = data?.playlistidPlaylists?.getKeyValue()?.toString()

    instance.downloadedAt = data?.downloadedAt
    instance.expiresAt = data?.expiresAt
    return instance
  }

  static parseList(data?: Download[] | null): DownloadFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => DownloadFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Download>> {
    const entity: Partial<Download> = new Download({
      id: this.id,

      downloadedAt: this.downloadedAt,

      expiresAt: this.expiresAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.playlistidPlaylists && this.playlistidPlaylists.length > 0) {
      const related = await playlistService.getById(Number(this.playlistidPlaylists))
      entity.playlistidPlaylists = related.data as Playlist
    }
    return entity
  }

  static async toEntities(data?: DownloadFormDTO[] | null) {
    const entities: Promise<Partial<Download>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
