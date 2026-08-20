import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'

export interface IEditorialPlaylist {
  id?: number
  artistidArtists?: Artist
  title?: string
  description?: string
  coverUrl?: string
  isFeatured?: boolean
  createdAt?: Date
}

export class EditorialPlaylist extends BaseModel implements IEditorialPlaylist {
  id?: number
  artistidArtists?: Artist
  title?: string
  description?: string
  coverUrl?: string
  isFeatured?: boolean
  createdAt?: Date

  constructor(data?: Partial<IEditorialPlaylist>) {
    super()
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.title = data?.title
    this.description = data?.description
    this.coverUrl = data?.coverUrl
    this.isFeatured = data?.isFeatured
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
      key: 'title',
      label: 'Title',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'description',
      label: 'Description',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'coverUrl',
      label: 'Cover url',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'isFeatured',
      label: 'Is featured',
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

export class EditorialPlaylistFormDTO {
  id?: number
  artistidArtists?: string

  title?: string
  description?: string
  coverUrl?: string
  isFeatured?: boolean
  createdAt?: Date

  constructor(data?: Partial<EditorialPlaylistFormDTO>) {
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ?? ''

    this.title = data?.title
    this.description = data?.description
    this.coverUrl = data?.coverUrl
    this.isFeatured = data?.isFeatured
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IEditorialPlaylist> | IEditorialPlaylist | null) {
    const instance = new EditorialPlaylistFormDTO()
    instance.id = data?.id
    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.title = data?.title
    instance.description = data?.description
    instance.coverUrl = data?.coverUrl
    instance.isFeatured = data?.isFeatured
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: EditorialPlaylist[] | null): EditorialPlaylistFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => EditorialPlaylistFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<EditorialPlaylist>> {
    const entity: Partial<EditorialPlaylist> = new EditorialPlaylist({
      id: this.id,

      title: this.title,

      description: this.description,

      coverUrl: this.coverUrl,

      isFeatured: this.isFeatured,

      createdAt: this.createdAt,
    })

    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    return entity
  }

  static async toEntities(data?: EditorialPlaylistFormDTO[] | null) {
    const entities: Promise<Partial<EditorialPlaylist>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
