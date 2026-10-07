import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Playlist } from './PlaylistModel'
import * as playlistService from '@/services/PlaylistService'
import { Song } from './SongModel'
import * as songService from '@/services/SongService'

export interface IPlaylistSong {
  id?: number
  playlistidPlaylists?: Playlist
  songidSongs?: Song
  position?: number
  addedAt?: Date
}

export class PlaylistSong extends BaseModel implements IPlaylistSong {
  id?: number
  playlistidPlaylists?: Playlist
  songidSongs?: Song
  position?: number
  addedAt?: Date

  constructor(data?: Partial<IPlaylistSong>) {
    super()
    this.id = data?.id
    this.playlistidPlaylists = data?.playlistidPlaylists
      ? new Playlist(data?.playlistidPlaylists)
      : undefined
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

export class PlaylistSongFormDTO {
  id?: number
  playlistidPlaylists?: string

  songidSongs?: string

  position?: number
  addedAt?: Date

  constructor(data?: Partial<PlaylistSongFormDTO>) {
    this.id = data?.id
    this.playlistidPlaylists = data?.playlistidPlaylists ?? ''

    this.songidSongs = data?.songidSongs ?? ''

    this.position = data?.position
    this.addedAt = data?.addedAt
  }

  static parse(data?: Partial<IPlaylistSong> | IPlaylistSong | null) {
    const instance = new PlaylistSongFormDTO()
    instance.id = data?.id
    instance.playlistidPlaylists = data?.playlistidPlaylists?.getKeyValue()?.toString()

    instance.songidSongs = data?.songidSongs?.getKeyValue()?.toString()

    instance.position = data?.position
    instance.addedAt = data?.addedAt
    return instance
  }

  static parseList(data?: PlaylistSong[] | null): PlaylistSongFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => PlaylistSongFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<PlaylistSong>> {
    const entity: Partial<PlaylistSong> = new PlaylistSong({
      id: this.id,

      position: this.position,

      addedAt: this.addedAt,
    })

    if (this.playlistidPlaylists && this.playlistidPlaylists.length > 0) {
      const related = await playlistService.getById(Number(this.playlistidPlaylists))
      entity.playlistidPlaylists = related.data as Playlist
    }
    if (this.songidSongs && this.songidSongs.length > 0) {
      const related = await songService.getById(Number(this.songidSongs))
      entity.songidSongs = related.data as Song
    }
    return entity
  }

  static async toEntities(data?: PlaylistSongFormDTO[] | null) {
    const entities: Promise<Partial<PlaylistSong>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
