import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IMediaType {
  id?: number
  name?: string
}

export class MediaType extends BaseModel implements IMediaType {
  id?: number
  name?: string

  constructor(data?: Partial<IMediaType>) {
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

export class MediaTypeFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<MediaTypeFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IMediaType> | IMediaType | null) {
    const instance = new MediaTypeFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: MediaType[] | null): MediaTypeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => MediaTypeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<MediaType>> {
    const entity: Partial<MediaType> = new MediaType({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: MediaTypeFormDTO[] | null) {
    const entities: Promise<Partial<MediaType>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
