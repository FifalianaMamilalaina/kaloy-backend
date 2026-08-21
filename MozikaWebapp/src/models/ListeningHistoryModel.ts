import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { Song } from './SongModel'
import * as songService from '@/services/SongService'
import { PlayMode } from './PlayModeModel'
import * as playModeService from '@/services/PlayModeService'

export interface IListeningHistory {
  id?: number
  useridUsers?: User
  songidSongs?: Song
  playmodeidPlayModes?: PlayMode
  listenedAt?: Date
}

export class ListeningHistory extends BaseModel implements IListeningHistory {
  id?: number
  useridUsers?: User
  songidSongs?: Song
  playmodeidPlayModes?: PlayMode
  listenedAt?: Date

  constructor(data?: Partial<IListeningHistory>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.songidSongs = data?.songidSongs ? new Song(data?.songidSongs) : undefined
    this.playmodeidPlayModes = data?.playmodeidPlayModes
      ? new PlayMode(data?.playmodeidPlayModes)
      : undefined
    this.listenedAt = data?.listenedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'listenedAt'
  }

  public override getReferenceValue(): string {
    return String(this.listenedAt)
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
      key: 'songidSongs',
      label: 'Song',
      type: 'select',
      searchKey: Song.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Song, songService),
      multicriteriaSelect: {
        filters: Song.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(songService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'playmodeidPlayModes',
      label: 'Play mode',
      type: 'select',
      searchKey: PlayMode.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(PlayMode, playModeService),
      multicriteriaSelect: {
        filters: PlayMode.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(playModeService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'listenedAt',
      label: 'Listened at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class ListeningHistoryFormDTO {
  id?: number
  useridUsers?: string

  songidSongs?: string

  playmodeidPlayModes?: string

  listenedAt?: Date

  constructor(data?: Partial<ListeningHistoryFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.songidSongs = data?.songidSongs ?? ''

    this.playmodeidPlayModes = data?.playmodeidPlayModes ?? ''

    this.listenedAt = data?.listenedAt
  }

  static parse(data?: Partial<IListeningHistory> | IListeningHistory | null) {
    const instance = new ListeningHistoryFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.songidSongs = data?.songidSongs?.getKeyValue()?.toString()

    instance.playmodeidPlayModes = data?.playmodeidPlayModes?.getKeyValue()?.toString()

    instance.listenedAt = data?.listenedAt
    return instance
  }

  static parseList(data?: ListeningHistory[] | null): ListeningHistoryFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ListeningHistoryFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ListeningHistory>> {
    const entity: Partial<ListeningHistory> = new ListeningHistory({
      id: this.id,

      listenedAt: this.listenedAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.songidSongs && this.songidSongs.length > 0) {
      const related = await songService.getById(Number(this.songidSongs))
      entity.songidSongs = related.data as Song
    }
    if (this.playmodeidPlayModes && this.playmodeidPlayModes.length > 0) {
      const related = await playModeService.getById(Number(this.playmodeidPlayModes))
      entity.playmodeidPlayModes = related.data as PlayMode
    }
    return entity
  }

  static async toEntities(data?: ListeningHistoryFormDTO[] | null) {
    const entities: Promise<Partial<ListeningHistory>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
