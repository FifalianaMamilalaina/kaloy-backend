import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { Song } from './SongModel'
import * as songService from '@/services/SongService'

export interface IUpNextQueue {
  id?: number
  useridUsers?: User
  songidSongs?: Song
  position?: number
  addedAt?: Date
}

export class UpNextQueue extends BaseModel implements IUpNextQueue {
  id?: number
  useridUsers?: User
  songidSongs?: Song
  position?: number
  addedAt?: Date

  constructor(data?: Partial<IUpNextQueue>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.songidSongs = data?.songidSongs ? new Song(data?.songidSongs) : undefined
    this.position = data?.position
    this.addedAt = data?.addedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'position'
  }

  public override getReferenceValue(): string {
    return String(this.position)
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
      key: 'position',
      label: 'Position',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'positionMin',
      label: 'Position Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'positionMax',
      label: 'Position Max',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'addedAt',
      label: 'Added at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class UpNextQueueFormDTO {
  id?: number
  useridUsers?: string

  songidSongs?: string

  position?: number
  addedAt?: Date

  constructor(data?: Partial<UpNextQueueFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.songidSongs = data?.songidSongs ?? ''

    this.position = data?.position
    this.addedAt = data?.addedAt
  }

  static parse(data?: Partial<IUpNextQueue> | IUpNextQueue | null) {
    const instance = new UpNextQueueFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.songidSongs = data?.songidSongs?.getKeyValue()?.toString()

    instance.position = data?.position
    instance.addedAt = data?.addedAt
    return instance
  }

  static parseList(data?: UpNextQueue[] | null): UpNextQueueFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => UpNextQueueFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<UpNextQueue>> {
    const entity: Partial<UpNextQueue> = new UpNextQueue({
      id: this.id,

      position: this.position,

      addedAt: this.addedAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.songidSongs && this.songidSongs.length > 0) {
      const related = await songService.getById(Number(this.songidSongs))
      entity.songidSongs = related.data as Song
    }
    return entity
  }

  static async toEntities(data?: UpNextQueueFormDTO[] | null) {
    const entities: Promise<Partial<UpNextQueue>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
