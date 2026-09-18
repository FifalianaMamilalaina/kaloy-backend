import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IUserRole {
  id?: number
  name?: string
}

export class UserRole extends BaseModel implements IUserRole {
  id?: number
  name?: string

  constructor(data?: Partial<IUserRole>) {
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

export class UserRoleFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<UserRoleFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IUserRole> | IUserRole | null) {
    const instance = new UserRoleFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: UserRole[] | null): UserRoleFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => UserRoleFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<UserRole>> {
    const entity: Partial<UserRole> = new UserRole({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: UserRoleFormDTO[] | null) {
    const entities: Promise<Partial<UserRole>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
