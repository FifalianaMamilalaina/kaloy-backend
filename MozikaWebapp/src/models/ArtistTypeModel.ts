import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IArtistType {
  id?: number
  name?: string
}

export class ArtistType extends BaseModel implements IArtistType {
  id?: number
  name?: string

  constructor(data?: Partial<IArtistType>) {
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

export class ArtistTypeFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<ArtistTypeFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IArtistType> | IArtistType | null) {
    const instance = new ArtistTypeFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: ArtistType[] | null): ArtistTypeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ArtistTypeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ArtistType>> {
    const entity: Partial<ArtistType> = new ArtistType({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: ArtistTypeFormDTO[] | null) {
    const entities: Promise<Partial<ArtistType>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
