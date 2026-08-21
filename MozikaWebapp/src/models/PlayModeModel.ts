import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IPlayMode {
  id?: number
  name?: string
}

export class PlayMode extends BaseModel implements IPlayMode {
  id?: number
  name?: string

  constructor(data?: Partial<IPlayMode>) {
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

export class PlayModeFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<PlayModeFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IPlayMode> | IPlayMode | null) {
    const instance = new PlayModeFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: PlayMode[] | null): PlayModeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => PlayModeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<PlayMode>> {
    const entity: Partial<PlayMode> = new PlayMode({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: PlayModeFormDTO[] | null) {
    const entities: Promise<Partial<PlayMode>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
