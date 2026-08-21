import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { UserStatuse } from './UserStatuseModel'
import * as userStatuseService from '@/services/UserStatuseService'

export interface IUserStatusHistory {
  id?: number
  useridUsers?: User
  previousstatusidUserStatuses?: UserStatuse
  newstatusidUserStatuses?: UserStatuse
  reason?: string
  changedbyuseridUsers?: User
  createdAt?: Date
}

export class UserStatusHistory extends BaseModel implements IUserStatusHistory {
  id?: number
  useridUsers?: User
  previousstatusidUserStatuses?: UserStatuse
  newstatusidUserStatuses?: UserStatuse
  reason?: string
  changedbyuseridUsers?: User
  createdAt?: Date

  constructor(data?: Partial<IUserStatusHistory>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.previousstatusidUserStatuses = data?.previousstatusidUserStatuses
      ? new UserStatuse(data?.previousstatusidUserStatuses)
      : undefined
    this.newstatusidUserStatuses = data?.newstatusidUserStatuses
      ? new UserStatuse(data?.newstatusidUserStatuses)
      : undefined
    this.reason = data?.reason
    this.changedbyuseridUsers = data?.changedbyuseridUsers
      ? new User(data?.changedbyuseridUsers)
      : undefined
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'reason'
  }

  public override getReferenceValue(): string {
    return String(this.reason)
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
      key: 'previousstatusidUserStatuses',
      label: 'User statuse',
      type: 'select',
      searchKey: UserStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(UserStatuse, userStatuseService),
      multicriteriaSelect: {
        filters: UserStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(userStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'newstatusidUserStatuses',
      label: 'User statuse',
      type: 'select',
      searchKey: UserStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(UserStatuse, userStatuseService),
      multicriteriaSelect: {
        filters: UserStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(userStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'reason',
      label: 'Reason',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'changedbyuseridUsers',
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
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class UserStatusHistoryFormDTO {
  id?: number
  useridUsers?: string

  previousstatusidUserStatuses?: string

  newstatusidUserStatuses?: string

  reason?: string
  changedbyuseridUsers?: string

  createdAt?: Date

  constructor(data?: Partial<UserStatusHistoryFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.previousstatusidUserStatuses = data?.previousstatusidUserStatuses ?? ''

    this.newstatusidUserStatuses = data?.newstatusidUserStatuses ?? ''

    this.reason = data?.reason
    this.changedbyuseridUsers = data?.changedbyuseridUsers ?? ''

    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IUserStatusHistory> | IUserStatusHistory | null) {
    const instance = new UserStatusHistoryFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.previousstatusidUserStatuses = data?.previousstatusidUserStatuses
      ?.getKeyValue()
      ?.toString()

    instance.newstatusidUserStatuses = data?.newstatusidUserStatuses?.getKeyValue()?.toString()

    instance.reason = data?.reason
    instance.changedbyuseridUsers = data?.changedbyuseridUsers?.getKeyValue()?.toString()

    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: UserStatusHistory[] | null): UserStatusHistoryFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => UserStatusHistoryFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<UserStatusHistory>> {
    const entity: Partial<UserStatusHistory> = new UserStatusHistory({
      id: this.id,

      reason: this.reason,

      createdAt: this.createdAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.previousstatusidUserStatuses && this.previousstatusidUserStatuses.length > 0) {
      const related = await userStatuseService.getById(Number(this.previousstatusidUserStatuses))
      entity.previousstatusidUserStatuses = related.data as UserStatuse
    }
    if (this.newstatusidUserStatuses && this.newstatusidUserStatuses.length > 0) {
      const related = await userStatuseService.getById(Number(this.newstatusidUserStatuses))
      entity.newstatusidUserStatuses = related.data as UserStatuse
    }
    if (this.changedbyuseridUsers && this.changedbyuseridUsers.length > 0) {
      const related = await userService.getById(Number(this.changedbyuseridUsers))
      entity.changedbyuseridUsers = related.data as User
    }
    return entity
  }

  static async toEntities(data?: UserStatusHistoryFormDTO[] | null) {
    const entities: Promise<Partial<UserStatusHistory>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
