import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'

export interface ISearchHistory {
  id?: number
  useridUsers?: User
  queryText?: string
  searchedAt?: Date
}

export class SearchHistory extends BaseModel implements ISearchHistory {
  id?: number
  useridUsers?: User
  queryText?: string
  searchedAt?: Date

  constructor(data?: Partial<ISearchHistory>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.queryText = data?.queryText
    this.searchedAt = data?.searchedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'queryText'
  }

  public override getReferenceValue(): string {
    return String(this.queryText)
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

    {
      key: 'queryText',
      label: 'Query text',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'searchedAt',
      label: 'Searched at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class SearchHistoryFormDTO {
  id?: number
  useridUsers?: string

  queryText?: string
  searchedAt?: Date

  constructor(data?: Partial<SearchHistoryFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.queryText = data?.queryText
    this.searchedAt = data?.searchedAt
  }

  static parse(data?: Partial<ISearchHistory> | ISearchHistory | null) {
    const instance = new SearchHistoryFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.queryText = data?.queryText
    instance.searchedAt = data?.searchedAt
    return instance
  }

  static parseList(data?: SearchHistory[] | null): SearchHistoryFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => SearchHistoryFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<SearchHistory>> {
    const entity: Partial<SearchHistory> = new SearchHistory({
      id: this.id,

      queryText: this.queryText,

      searchedAt: this.searchedAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    return entity
  }

  static async toEntities(data?: SearchHistoryFormDTO[] | null) {
    const entities: Promise<Partial<SearchHistory>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
