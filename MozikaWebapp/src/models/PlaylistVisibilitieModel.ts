import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IPlaylistVisibilitie {
  id?: number
  name?: string
}

export class PlaylistVisibilitie extends BaseModel implements IPlaylistVisibilitie {
  id?: number
  name?: string

  constructor(data?: Partial<IPlaylistVisibilitie>) {
    super()
    this.id = data?.id
    this.name = data?.name
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
      key: 'name',
      label: 'Name',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class PlaylistVisibilitieFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<PlaylistVisibilitieFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IPlaylistVisibilitie> | IPlaylistVisibilitie | null) {
    const instance = new PlaylistVisibilitieFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: PlaylistVisibilitie[] | null): PlaylistVisibilitieFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => PlaylistVisibilitieFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<PlaylistVisibilitie>> {
    const entity: Partial<PlaylistVisibilitie> = new PlaylistVisibilitie({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: PlaylistVisibilitieFormDTO[] | null) {
    const entities: Promise<Partial<PlaylistVisibilitie>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
