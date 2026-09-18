import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Song } from './SongModel'
import * as songService from '@/services/SongService'
import { Genre } from './GenreModel'
import * as genreService from '@/services/GenreService'

export interface ISongGenre {
  id?: number
  songidSongs?: Song
  genreidGenres?: Genre
}

export class SongGenre extends BaseModel implements ISongGenre {
  id?: number
  songidSongs?: Song
  genreidGenres?: Genre

  constructor(data?: Partial<ISongGenre>) {
    super()
    this.id = data?.id
    this.songidSongs = data?.songidSongs ? new Song(data?.songidSongs) : undefined
    this.genreidGenres = data?.genreidGenres ? new Genre(data?.genreidGenres) : undefined
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {}

  public override getReferenceValue(): string {}

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
      key: 'genreidGenres',
      label: 'Genre',
      type: 'select',
      searchKey: Genre.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Genre, genreService),
      multicriteriaSelect: {
        filters: Genre.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(genreService),
      },
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class SongGenreFormDTO {
  id?: number
  songidSongs?: string

  genreidGenres?: string

  constructor(data?: Partial<SongGenreFormDTO>) {
    this.id = data?.id
    this.songidSongs = data?.songidSongs ?? ''

    this.genreidGenres = data?.genreidGenres ?? ''
  }

  static parse(data?: Partial<ISongGenre> | ISongGenre | null) {
    const instance = new SongGenreFormDTO()
    instance.id = data?.id
    instance.songidSongs = data?.songidSongs?.getKeyValue()?.toString()

    instance.genreidGenres = data?.genreidGenres?.getKeyValue()?.toString()

    return instance
  }

  static parseList(data?: SongGenre[] | null): SongGenreFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => SongGenreFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<SongGenre>> {
    const entity: Partial<SongGenre> = new SongGenre({
      id: this.id,
    })

    if (this.songidSongs && this.songidSongs.length > 0) {
      const related = await songService.getById(Number(this.songidSongs))
      entity.songidSongs = related.data as Song
    }
    if (this.genreidGenres && this.genreidGenres.length > 0) {
      const related = await genreService.getById(Number(this.genreidGenres))
      entity.genreidGenres = related.data as Genre
    }
    return entity
  }

  static async toEntities(data?: SongGenreFormDTO[] | null) {
    const entities: Promise<Partial<SongGenre>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
