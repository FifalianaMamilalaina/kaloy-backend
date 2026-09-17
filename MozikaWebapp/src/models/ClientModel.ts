import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'

export interface IClient {
  id?: number
  useridUsers?: User
}

export class Client extends BaseModel implements IClient {
  id?: number
  useridUsers?: User

  constructor(data?: Partial<IClient>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {}

  public override getReferenceValue(): string {}

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
      key: 'useridUsers',
      label: 'User',
      type: 'select',
      searchKey: User.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(User, userService),
      multicriteriaSelect: {
        filters: User.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(userService),
      },
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class ClientFormDTO {
  id?: number
  useridUsers?: string

  constructor(data?: Partial<ClientFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''
  }

  static parse(data?: Partial<IClient> | IClient | null) {
    const instance = new ClientFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    return instance
  }

  static parseList(data?: Client[] | null): ClientFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ClientFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Client>> {
    const entity: Partial<Client> = new Client({
      id: this.id,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    return entity
  }

  static async toEntities(data?: ClientFormDTO[] | null) {
    const entities: Promise<Partial<Client>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
