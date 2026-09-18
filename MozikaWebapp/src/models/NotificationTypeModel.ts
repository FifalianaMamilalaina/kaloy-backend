import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface INotificationType {
  id?: number
  name?: string
}

export class NotificationType extends BaseModel implements INotificationType {
  id?: number
  name?: string

  constructor(data?: Partial<INotificationType>) {
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

export class NotificationTypeFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<NotificationTypeFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<INotificationType> | INotificationType | null) {
    const instance = new NotificationTypeFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: NotificationType[] | null): NotificationTypeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => NotificationTypeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<NotificationType>> {
    const entity: Partial<NotificationType> = new NotificationType({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: NotificationTypeFormDTO[] | null) {
    const entities: Promise<Partial<NotificationType>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
