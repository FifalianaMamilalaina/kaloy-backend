import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { NotificationType } from './NotificationTypeModel'
import * as notificationTypeService from '@/services/NotificationTypeService'
import { InteractionTarget } from './InteractionTargetModel'
import * as interactionTargetService from '@/services/InteractionTargetService'

export interface INotification {
  id?: number
  useridUsers?: User
  typeidNotificationTypes?: NotificationType
  content?: string
  relatedentitytypeidInteractionTargets?: InteractionTarget
  relatedEntityId?: number
  isRead?: boolean
  createdAt?: Date
}

export class Notification extends BaseModel implements INotification {
  id?: number
  useridUsers?: User
  typeidNotificationTypes?: NotificationType
  content?: string
  relatedentitytypeidInteractionTargets?: InteractionTarget
  relatedEntityId?: number
  isRead?: boolean
  createdAt?: Date

  constructor(data?: Partial<INotification>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.typeidNotificationTypes = data?.typeidNotificationTypes
      ? new NotificationType(data?.typeidNotificationTypes)
      : undefined
    this.content = data?.content
    this.relatedentitytypeidInteractionTargets = data?.relatedentitytypeidInteractionTargets
      ? new InteractionTarget(data?.relatedentitytypeidInteractionTargets)
      : undefined
    this.relatedEntityId = data?.relatedEntityId
    this.isRead = data?.isRead
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'content'
  }

  public override getReferenceValue(): string {
    return String(this.content)
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
      key: 'typeidNotificationTypes',
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
      key: 'content',
      label: 'Content',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'relatedentitytypeidInteractionTargets',
      label: 'Interaction target',
      type: 'select',
      searchKey: InteractionTarget.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(InteractionTarget, interactionTargetService),
      multicriteriaSelect: {
        filters: InteractionTarget.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(interactionTargetService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'relatedEntityId',
      label: 'Related entity id',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'relatedEntityIdMin',
      label: 'Related entity id Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'relatedEntityIdMax',
      label: 'Related entity id Max',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'isRead',
      label: 'Is read',
      type: 'checkbox',
      sortable: true,
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

export class NotificationFormDTO {
  id?: number
  useridUsers?: string

  typeidNotificationTypes?: string

  content?: string
  relatedentitytypeidInteractionTargets?: string

  relatedEntityId?: number
  isRead?: boolean
  createdAt?: Date

  constructor(data?: Partial<NotificationFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.typeidNotificationTypes = data?.typeidNotificationTypes ?? ''

    this.content = data?.content
    this.relatedentitytypeidInteractionTargets = data?.relatedentitytypeidInteractionTargets ?? ''

    this.relatedEntityId = data?.relatedEntityId
    this.isRead = data?.isRead
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<INotification> | INotification | null) {
    const instance = new NotificationFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.typeidNotificationTypes = data?.typeidNotificationTypes?.getKeyValue()?.toString()

    instance.content = data?.content
    instance.relatedentitytypeidInteractionTargets = data?.relatedentitytypeidInteractionTargets
      ?.getKeyValue()
      ?.toString()

    instance.relatedEntityId = data?.relatedEntityId
    instance.isRead = data?.isRead
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Notification[] | null): NotificationFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => NotificationFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Notification>> {
    const entity: Partial<Notification> = new Notification({
      id: this.id,

      content: this.content,

      relatedEntityId: this.relatedEntityId,

      isRead: this.isRead,

      createdAt: this.createdAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.typeidNotificationTypes && this.typeidNotificationTypes.length > 0) {
      const related = await notificationTypeService.getById(Number(this.typeidNotificationTypes))
      entity.typeidNotificationTypes = related.data as NotificationType
    }
    if (
      this.relatedentitytypeidInteractionTargets &&
      this.relatedentitytypeidInteractionTargets.length > 0
    ) {
      const related = await interactionTargetService.getById(
        Number(this.relatedentitytypeidInteractionTargets),
      )
      entity.relatedentitytypeidInteractionTargets = related.data as InteractionTarget
    }
    return entity
  }

  static async toEntities(data?: NotificationFormDTO[] | null) {
    const entities: Promise<Partial<Notification>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
