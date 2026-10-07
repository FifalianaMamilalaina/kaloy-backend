import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IEventModerationStatuse {
  id?: number
  name?: string
}

export class EventModerationStatuse extends BaseModel implements IEventModerationStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IEventModerationStatuse>) {
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

export class EventModerationStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<EventModerationStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IEventModerationStatuse> | IEventModerationStatuse | null) {
    const instance = new EventModerationStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: EventModerationStatuse[] | null): EventModerationStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => EventModerationStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<EventModerationStatuse>> {
    const entity: Partial<EventModerationStatuse> = new EventModerationStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: EventModerationStatuseFormDTO[] | null) {
    const entities: Promise<Partial<EventModerationStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
