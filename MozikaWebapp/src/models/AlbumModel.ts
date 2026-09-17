import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'

export interface IAlbum {
  id?: number
  artistidArtists?: Artist
  title?: string
  coverUrl?: string
  releaseDate?: string
  createdAt?: Date
}

export class Album extends BaseModel implements IAlbum {
  id?: number
  artistidArtists?: Artist
  title?: string
  coverUrl?: string
  releaseDate?: string
  createdAt?: Date

  constructor(data?: Partial<IAlbum>) {
    super()
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.title = data?.title
    this.coverUrl = data?.coverUrl
    this.releaseDate = data?.releaseDate
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
      key: 'coverUrl',
      label: 'Cover url',
      type: 'text',
      sortable: true,
      showInTable: true,
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
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class AlbumFormDTO {
  id?: number
  artistidArtists?: string

  title?: string
  coverUrl?: string
  releaseDate?: string
  createdAt?: Date

  constructor(data?: Partial<AlbumFormDTO>) {
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ?? ''

    this.title = data?.title
    this.coverUrl = data?.coverUrl
    this.releaseDate = data?.releaseDate ? data.releaseDate : undefined

    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IAlbum> | IAlbum | null) {
    const instance = new AlbumFormDTO()
    instance.id = data?.id
    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.title = data?.title
    instance.coverUrl = data?.coverUrl
    instance.releaseDate = data?.releaseDate
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Album[] | null): AlbumFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => AlbumFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Album>> {
    const entity: Partial<Album> = new Album({
      id: this.id,

      title: this.title,

      coverUrl: this.coverUrl,

      releaseDate: this.releaseDate ? this.releaseDate.split('T')[0] : undefined,

      createdAt: this.createdAt,
    })

    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    return entity
  }

  static async toEntities(data?: AlbumFormDTO[] | null) {
    const entities: Promise<Partial<Album>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
