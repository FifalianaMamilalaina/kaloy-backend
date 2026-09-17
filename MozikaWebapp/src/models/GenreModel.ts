import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IGenre {
  id?: number
  name?: string
}

export class Genre extends BaseModel implements IGenre {
  id?: number
  name?: string

  constructor(data?: Partial<IGenre>) {
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

export class GenreFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<GenreFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IGenre> | IGenre | null) {
    const instance = new GenreFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: Genre[] | null): GenreFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => GenreFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Genre>> {
    const entity: Partial<Genre> = new Genre({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: GenreFormDTO[] | null) {
    const entities: Promise<Partial<Genre>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
