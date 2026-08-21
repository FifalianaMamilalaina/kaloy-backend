import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IAudioStorageType {
  id?: number
  name?: string
}

export class AudioStorageType extends BaseModel implements IAudioStorageType {
  id?: number
  name?: string

  constructor(data?: Partial<IAudioStorageType>) {
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

export class AudioStorageTypeFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<AudioStorageTypeFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IAudioStorageType> | IAudioStorageType | null) {
    const instance = new AudioStorageTypeFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: AudioStorageType[] | null): AudioStorageTypeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => AudioStorageTypeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<AudioStorageType>> {
    const entity: Partial<AudioStorageType> = new AudioStorageType({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: AudioStorageTypeFormDTO[] | null) {
    const entities: Promise<Partial<AudioStorageType>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
