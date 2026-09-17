import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'

export interface IFollow {
  id?: number
  clientuseridUsers?: User
  artistidArtists?: Artist
  createdAt?: Date
}

export class Follow extends BaseModel implements IFollow {
  id?: number
  clientuseridUsers?: User
  artistidArtists?: Artist
  createdAt?: Date

  constructor(data?: Partial<IFollow>) {
    super()
    this.id = data?.id
    this.clientuseridUsers = data?.clientuseridUsers ? new User(data?.clientuseridUsers) : undefined
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'createdAt'
  }

  public override getReferenceValue(): string {
    return String(this.createdAt)
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
      key: 'clientuseridUsers',
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
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class FollowFormDTO {
  id?: number
  clientuseridUsers?: string

  artistidArtists?: string

  createdAt?: Date

  constructor(data?: Partial<FollowFormDTO>) {
    this.id = data?.id
    this.clientuseridUsers = data?.clientuseridUsers ?? ''

    this.artistidArtists = data?.artistidArtists ?? ''

    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IFollow> | IFollow | null) {
    const instance = new FollowFormDTO()
    instance.id = data?.id
    instance.clientuseridUsers = data?.clientuseridUsers?.getKeyValue()?.toString()

    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Follow[] | null): FollowFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => FollowFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Follow>> {
    const entity: Partial<Follow> = new Follow({
      id: this.id,

      createdAt: this.createdAt,
    })

    if (this.clientuseridUsers && this.clientuseridUsers.length > 0) {
      const related = await userService.getById(Number(this.clientuseridUsers))
      entity.clientuseridUsers = related.data as User
    }
    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    return entity
  }

  static async toEntities(data?: FollowFormDTO[] | null) {
    const entities: Promise<Partial<Follow>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
