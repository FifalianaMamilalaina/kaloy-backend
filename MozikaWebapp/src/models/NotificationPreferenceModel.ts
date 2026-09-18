import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { NotificationType } from './NotificationTypeModel'
import * as notificationTypeService from '@/services/NotificationTypeService'

export interface INotificationPreference {
  id?: number
  useridUsers?: User
  notificationtypeidNotificationTypes?: NotificationType
  isEnabled?: boolean
}

export class NotificationPreference extends BaseModel implements INotificationPreference {
  id?: number
  useridUsers?: User
  notificationtypeidNotificationTypes?: NotificationType
  isEnabled?: boolean

  constructor(data?: Partial<INotificationPreference>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.notificationtypeidNotificationTypes = data?.notificationtypeidNotificationTypes
      ? new NotificationType(data?.notificationtypeidNotificationTypes)
      : undefined
    this.isEnabled = data?.isEnabled
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'isEnabled'
  }

  public override getReferenceValue(): string {
    return String(this.isEnabled)
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
      key: 'notificationtypeidNotificationTypes',
      label: 'Notification type',
      type: 'select',
      searchKey: NotificationType.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(NotificationType, notificationTypeService),
      multicriteriaSelect: {
        filters: NotificationType.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(notificationTypeService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'isEnabled',
      label: 'Is enabled',
      type: 'checkbox',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class NotificationPreferenceFormDTO {
  id?: number
  useridUsers?: string

  notificationtypeidNotificationTypes?: string

  isEnabled?: boolean

  constructor(data?: Partial<NotificationPreferenceFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.notificationtypeidNotificationTypes = data?.notificationtypeidNotificationTypes ?? ''

    this.isEnabled = data?.isEnabled
  }

  static parse(data?: Partial<INotificationPreference> | INotificationPreference | null) {
    const instance = new NotificationPreferenceFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.notificationtypeidNotificationTypes = data?.notificationtypeidNotificationTypes
      ?.getKeyValue()
      ?.toString()

    instance.isEnabled = data?.isEnabled
    return instance
  }

  static parseList(data?: NotificationPreference[] | null): NotificationPreferenceFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => NotificationPreferenceFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<NotificationPreference>> {
    const entity: Partial<NotificationPreference> = new NotificationPreference({
      id: this.id,

      isEnabled: this.isEnabled,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (
      this.notificationtypeidNotificationTypes &&
      this.notificationtypeidNotificationTypes.length > 0
    ) {
      const related = await notificationTypeService.getById(
        Number(this.notificationtypeidNotificationTypes),
      )
      entity.notificationtypeidNotificationTypes = related.data as NotificationType
    }
    return entity
  }

  static async toEntities(data?: NotificationPreferenceFormDTO[] | null) {
    const entities: Promise<Partial<NotificationPreference>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
