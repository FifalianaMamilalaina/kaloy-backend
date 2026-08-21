import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { EditorialPlaylist } from './EditorialPlaylistModel'
import * as editorialPlaylistService from '@/services/EditorialPlaylistService'
import { Song } from './SongModel'
import * as songService from '@/services/SongService'

export interface IEditorialPlaylistSong {
  id?: number
  editorialplaylistidEditorialPlaylists?: EditorialPlaylist
  songidSongs?: Song
  position?: number
}

export class EditorialPlaylistSong extends BaseModel implements IEditorialPlaylistSong {
  id?: number
  editorialplaylistidEditorialPlaylists?: EditorialPlaylist
  songidSongs?: Song
  position?: number

  constructor(data?: Partial<IEditorialPlaylistSong>) {
    super()
    this.id = data?.id
    this.editorialplaylistidEditorialPlaylists = data?.editorialplaylistidEditorialPlaylists
      ? new EditorialPlaylist(data?.editorialplaylistidEditorialPlaylists)
      : undefined
    this.songidSongs = data?.songidSongs ? new Song(data?.songidSongs) : undefined
    this.position = data?.position
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
      key: 'editorialplaylistidEditorialPlaylists',
      label: 'Editorial playlist',
      type: 'select',
      searchKey: EditorialPlaylist.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(EditorialPlaylist, editorialPlaylistService),
      multicriteriaSelect: {
        filters: EditorialPlaylist.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(editorialPlaylistService),
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
  ]
}

export class EditorialPlaylistSongFormDTO {
  id?: number
  editorialplaylistidEditorialPlaylists?: string

  songidSongs?: string

  position?: number

  constructor(data?: Partial<EditorialPlaylistSongFormDTO>) {
    this.id = data?.id
    this.editorialplaylistidEditorialPlaylists = data?.editorialplaylistidEditorialPlaylists ?? ''

    this.songidSongs = data?.songidSongs ?? ''

    this.position = data?.position
  }

  static parse(data?: Partial<IEditorialPlaylistSong> | IEditorialPlaylistSong | null) {
    const instance = new EditorialPlaylistSongFormDTO()
    instance.id = data?.id
    instance.editorialplaylistidEditorialPlaylists = data?.editorialplaylistidEditorialPlaylists
      ?.getKeyValue()
      ?.toString()

    instance.songidSongs = data?.songidSongs?.getKeyValue()?.toString()

    instance.position = data?.position
    return instance
  }

  static parseList(data?: EditorialPlaylistSong[] | null): EditorialPlaylistSongFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => EditorialPlaylistSongFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<EditorialPlaylistSong>> {
    const entity: Partial<EditorialPlaylistSong> = new EditorialPlaylistSong({
      id: this.id,

      position: this.position,
    })

    if (
      this.editorialplaylistidEditorialPlaylists &&
      this.editorialplaylistidEditorialPlaylists.length > 0
    ) {
      const related = await editorialPlaylistService.getById(
        Number(this.editorialplaylistidEditorialPlaylists),
      )
      entity.editorialplaylistidEditorialPlaylists = related.data as EditorialPlaylist
    }
    if (this.songidSongs && this.songidSongs.length > 0) {
      const related = await songService.getById(Number(this.songidSongs))
      entity.songidSongs = related.data as Song
    }
    return entity
  }

  static async toEntities(data?: EditorialPlaylistSongFormDTO[] | null) {
    const entities: Promise<Partial<EditorialPlaylistSong>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
