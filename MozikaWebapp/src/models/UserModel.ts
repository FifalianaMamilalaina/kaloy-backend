import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { UserRole } from './UserRoleModel'
import * as userRoleService from '@/services/UserRoleService'
import { UserStatuse } from './UserStatuseModel'
import * as userStatuseService from '@/services/UserStatuseService'

export interface IUser {
  id?: number
  email?: string
  phone?: string
  emailVerifiedAt?: Date
  phoneVerifiedAt?: Date
  passwordHash?: string
  roleidUserRoles?: UserRole
  statusidUserStatuses?: UserStatuse
  createdAt?: Date
  updatedAt?: Date
}

export class User extends BaseModel implements IUser {
  id?: number
  email?: string
  phone?: string
  emailVerifiedAt?: Date
  phoneVerifiedAt?: Date
  passwordHash?: string
  roleidUserRoles?: UserRole
  statusidUserStatuses?: UserStatuse
  createdAt?: Date
  updatedAt?: Date

  constructor(data?: Partial<IUser>) {
    super()
    this.id = data?.id
    this.email = data?.email
    this.phone = data?.phone
    this.emailVerifiedAt = data?.emailVerifiedAt
    this.phoneVerifiedAt = data?.phoneVerifiedAt
    this.passwordHash = data?.passwordHash
    this.roleidUserRoles = data?.roleidUserRoles ? new UserRole(data?.roleidUserRoles) : undefined
    this.statusidUserStatuses = data?.statusidUserStatuses
      ? new UserStatuse(data?.statusidUserStatuses)
      : undefined
    this.createdAt = data?.createdAt
    this.updatedAt = data?.updatedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'email'
  }

  public override getReferenceValue(): string {
    return String(this.email)
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
      key: 'email',
      label: 'Email',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'phone',
      label: 'Phone',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'emailVerifiedAt',
      label: 'Email verified at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'phoneVerifiedAt',
      label: 'Phone verified at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'passwordHash',
      label: 'Password hash',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'roleidUserRoles',
      label: 'User role',
      type: 'select',
      searchKey: UserRole.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(UserRole, userRoleService),
      multicriteriaSelect: {
        filters: UserRole.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(userRoleService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'statusidUserStatuses',
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
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'updatedAt',
      label: 'Updated at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class UserFormDTO {
  id?: number
  email?: string
  phone?: string
  emailVerifiedAt?: Date
  phoneVerifiedAt?: Date
  passwordHash?: string
  roleidUserRoles?: string

  statusidUserStatuses?: string

  createdAt?: Date
  updatedAt?: Date

  constructor(data?: Partial<UserFormDTO>) {
    this.id = data?.id
    this.email = data?.email
    this.phone = data?.phone
    this.emailVerifiedAt = data?.emailVerifiedAt
    this.phoneVerifiedAt = data?.phoneVerifiedAt
    this.passwordHash = data?.passwordHash
    this.roleidUserRoles = data?.roleidUserRoles ?? ''

    this.statusidUserStatuses = data?.statusidUserStatuses ?? ''

    this.createdAt = data?.createdAt
    this.updatedAt = data?.updatedAt
  }

  static parse(data?: Partial<IUser> | IUser | null) {
    const instance = new UserFormDTO()
    instance.id = data?.id
    instance.email = data?.email
    instance.phone = data?.phone
    instance.emailVerifiedAt = data?.emailVerifiedAt
    instance.phoneVerifiedAt = data?.phoneVerifiedAt
    instance.passwordHash = data?.passwordHash
    instance.roleidUserRoles = data?.roleidUserRoles?.getKeyValue()?.toString()

    instance.statusidUserStatuses = data?.statusidUserStatuses?.getKeyValue()?.toString()

    instance.createdAt = data?.createdAt
    instance.updatedAt = data?.updatedAt
    return instance
  }

  static parseList(data?: User[] | null): UserFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => UserFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<User>> {
    const entity: Partial<User> = new User({
      id: this.id,

      email: this.email,

      phone: this.phone,

      emailVerifiedAt: this.emailVerifiedAt,

      phoneVerifiedAt: this.phoneVerifiedAt,

      passwordHash: this.passwordHash,

      createdAt: this.createdAt,

      updatedAt: this.updatedAt,
    })

    if (this.roleidUserRoles && this.roleidUserRoles.length > 0) {
      const related = await userRoleService.getById(Number(this.roleidUserRoles))
      entity.roleidUserRoles = related.data as UserRole
    }
    if (this.statusidUserStatuses && this.statusidUserStatuses.length > 0) {
      const related = await userStatuseService.getById(Number(this.statusidUserStatuses))
      entity.statusidUserStatuses = related.data as UserStatuse
    }
    return entity
  }

  static async toEntities(data?: UserFormDTO[] | null) {
    const entities: Promise<Partial<User>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
