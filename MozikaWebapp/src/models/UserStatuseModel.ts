import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IUserStatuse {
  id?: number
  name?: string
}

export class UserStatuse extends BaseModel implements IUserStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IUserStatuse>) {
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

export class UserStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<UserStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IUserStatuse> | IUserStatuse | null) {
    const instance = new UserStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: UserStatuse[] | null): UserStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => UserStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<UserStatuse>> {
    const entity: Partial<UserStatuse> = new UserStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: UserStatuseFormDTO[] | null) {
    const entities: Promise<Partial<UserStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
