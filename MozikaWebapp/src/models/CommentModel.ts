import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { InteractionTarget } from './InteractionTargetModel'
import * as interactionTargetService from '@/services/InteractionTargetService'

export interface IComment {
  id?: number
  authoruseridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  content?: string
  isHidden?: boolean
  createdAt?: Date
}

export class Comment extends BaseModel implements IComment {
  id?: number
  authoruseridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  content?: string
  isHidden?: boolean
  createdAt?: Date

  constructor(data?: Partial<IComment>) {
    super()
    this.id = data?.id
    this.authoruseridUsers = data?.authoruseridUsers ? new User(data?.authoruseridUsers) : undefined
    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ? new InteractionTarget(data?.targettypeidInteractionTargets)
      : undefined
    this.targetId = data?.targetId
    this.content = data?.content
    this.isHidden = data?.isHidden
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'targetId'
  }

  public override getReferenceValue(): string {
    return String(this.targetId)
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
      key: 'authoruseridUsers',
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
      key: 'targettypeidInteractionTargets',
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
      key: 'targetId',
      label: 'Target id',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'targetIdMin',
      label: 'Target id Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'targetIdMax',
      label: 'Target id Max',
      type: 'number',
      sortable: false,
      showInTable: false,
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
      key: 'isHidden',
      label: 'Is hidden',
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

export class CommentFormDTO {
  id?: number
  authoruseridUsers?: string

  targettypeidInteractionTargets?: string

  targetId?: number
  content?: string
  isHidden?: boolean
  createdAt?: Date

  constructor(data?: Partial<CommentFormDTO>) {
    this.id = data?.id
    this.authoruseridUsers = data?.authoruseridUsers ?? ''

    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets ?? ''

    this.targetId = data?.targetId
    this.content = data?.content
    this.isHidden = data?.isHidden
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IComment> | IComment | null) {
    const instance = new CommentFormDTO()
    instance.id = data?.id
    instance.authoruseridUsers = data?.authoruseridUsers?.getKeyValue()?.toString()

    instance.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ?.getKeyValue()
      ?.toString()

    instance.targetId = data?.targetId
    instance.content = data?.content
    instance.isHidden = data?.isHidden
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Comment[] | null): CommentFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => CommentFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Comment>> {
    const entity: Partial<Comment> = new Comment({
      id: this.id,

      targetId: this.targetId,

      content: this.content,

      isHidden: this.isHidden,

      createdAt: this.createdAt,
    })

    if (this.authoruseridUsers && this.authoruseridUsers.length > 0) {
      const related = await userService.getById(Number(this.authoruseridUsers))
      entity.authoruseridUsers = related.data as User
    }
    if (this.targettypeidInteractionTargets && this.targettypeidInteractionTargets.length > 0) {
      const related = await interactionTargetService.getById(
        Number(this.targettypeidInteractionTargets),
      )
      entity.targettypeidInteractionTargets = related.data as InteractionTarget
    }
    return entity
  }

  static async toEntities(data?: CommentFormDTO[] | null) {
    const entities: Promise<Partial<Comment>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
