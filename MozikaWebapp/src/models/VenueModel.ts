import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IVenue {
  id?: number
  name?: string
  location?: string
}

export class Venue extends BaseModel implements IVenue {
  id?: number
  name?: string
  location?: string

  constructor(data?: Partial<IVenue>) {
    super()
    this.id = data?.id
    this.name = data?.name
    this.location = data?.location
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
    {
      key: 'location',
      label: 'Location',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class VenueFormDTO {
  id?: number
  name?: string
  location?: string

  constructor(data?: Partial<VenueFormDTO>) {
    this.id = data?.id
    this.name = data?.name
    this.location = data?.location
  }

  static parse(data?: Partial<IVenue> | IVenue | null) {
    const instance = new VenueFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    instance.location = data?.location
    return instance
  }

  static parseList(data?: Venue[] | null): VenueFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => VenueFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Venue>> {
    const entity: Partial<Venue> = new Venue({
      id: this.id,

      name: this.name,

      location: this.location,
    })

    return entity
  }

  static async toEntities(data?: VenueFormDTO[] | null) {
    const entities: Promise<Partial<Venue>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
