import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { PlaylistVisibilitie } from './PlaylistVisibilitieModel'
import * as playlistVisibilitieService from '@/services/PlaylistVisibilitieService'

export interface IPlaylist {
  id?: number
  owneruseridUsers?: User
  name?: string
  visibilityidPlaylistVisibilities?: PlaylistVisibilitie
  shareToken?: string
  createdAt?: Date
  updatedAt?: Date
}

export class Playlist extends BaseModel implements IPlaylist {
  id?: number
  owneruseridUsers?: User
  name?: string
  visibilityidPlaylistVisibilities?: PlaylistVisibilitie
  shareToken?: string
  createdAt?: Date
  updatedAt?: Date

  constructor(data?: Partial<IPlaylist>) {
    super()
    this.id = data?.id
    this.owneruseridUsers = data?.owneruseridUsers ? new User(data?.owneruseridUsers) : undefined
    this.name = data?.name
    this.visibilityidPlaylistVisibilities = data?.visibilityidPlaylistVisibilities
      ? new PlaylistVisibilitie(data?.visibilityidPlaylistVisibilities)
      : undefined
    this.shareToken = data?.shareToken
    this.createdAt = data?.createdAt
    this.updatedAt = data?.updatedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'name'
  }

  public override getReferenceValue(): string {
    return String(this.name)
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
      key: 'owneruseridUsers',
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
      key: 'name',
      label: 'Name',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'visibilityidPlaylistVisibilities',
      label: 'Playlist visibilitie',
      type: 'select',
      searchKey: PlaylistVisibilitie.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(PlaylistVisibilitie, playlistVisibilitieService),
      multicriteriaSelect: {
        filters: PlaylistVisibilitie.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(playlistVisibilitieService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'shareToken',
      label: 'Share token',
      type: 'text',
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
    {
      key: 'updatedAt',
      label: 'Updated at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class PlaylistFormDTO {
  id?: number
  owneruseridUsers?: string

  name?: string
  visibilityidPlaylistVisibilities?: string

  shareToken?: string
  createdAt?: Date
  updatedAt?: Date

  constructor(data?: Partial<PlaylistFormDTO>) {
    this.id = data?.id
    this.owneruseridUsers = data?.owneruseridUsers ?? ''

    this.name = data?.name
    this.visibilityidPlaylistVisibilities = data?.visibilityidPlaylistVisibilities ?? ''

    this.shareToken = data?.shareToken
    this.createdAt = data?.createdAt
    this.updatedAt = data?.updatedAt
  }

  static parse(data?: Partial<IPlaylist> | IPlaylist | null) {
    const instance = new PlaylistFormDTO()
    instance.id = data?.id
    instance.owneruseridUsers = data?.owneruseridUsers?.getKeyValue()?.toString()

    instance.name = data?.name
    instance.visibilityidPlaylistVisibilities = data?.visibilityidPlaylistVisibilities
      ?.getKeyValue()
      ?.toString()

    instance.shareToken = data?.shareToken
    instance.createdAt = data?.createdAt
    instance.updatedAt = data?.updatedAt
    return instance
  }

  static parseList(data?: Playlist[] | null): PlaylistFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => PlaylistFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Playlist>> {
    const entity: Partial<Playlist> = new Playlist({
      id: this.id,

      name: this.name,

      shareToken: this.shareToken,

      createdAt: this.createdAt,

      updatedAt: this.updatedAt,
    })

    if (this.owneruseridUsers && this.owneruseridUsers.length > 0) {
      const related = await userService.getById(Number(this.owneruseridUsers))
      entity.owneruseridUsers = related.data as User
    }
    if (this.visibilityidPlaylistVisibilities && this.visibilityidPlaylistVisibilities.length > 0) {
      const related = await playlistVisibilitieService.getById(
        Number(this.visibilityidPlaylistVisibilities),
      )
      entity.visibilityidPlaylistVisibilities = related.data as PlaylistVisibilitie
    }
    return entity
  }

  static async toEntities(data?: PlaylistFormDTO[] | null) {
    const entities: Promise<Partial<Playlist>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
