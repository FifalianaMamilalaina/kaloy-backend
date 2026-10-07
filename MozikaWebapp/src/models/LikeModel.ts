import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { InteractionTarget } from './InteractionTargetModel'
import * as interactionTargetService from '@/services/InteractionTargetService'

export interface ILike {
  id?: number
  useridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  createdAt?: Date
}

export class Like extends BaseModel implements ILike {
  id?: number
  useridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  createdAt?: Date

  constructor(data?: Partial<ILike>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ? new InteractionTarget(data?.targettypeidInteractionTargets)
      : undefined
    this.targetId = data?.targetId
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
      key: 'createdAt',
      label: 'Created at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class LikeFormDTO {
  id?: number
  useridUsers?: string

  targettypeidInteractionTargets?: string

  targetId?: number
  createdAt?: Date

  constructor(data?: Partial<LikeFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets ?? ''

    this.targetId = data?.targetId
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<ILike> | ILike | null) {
    const instance = new LikeFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ?.getKeyValue()
      ?.toString()

    instance.targetId = data?.targetId
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Like[] | null): LikeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => LikeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Like>> {
    const entity: Partial<Like> = new Like({
      id: this.id,

      targetId: this.targetId,

      createdAt: this.createdAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.targettypeidInteractionTargets && this.targettypeidInteractionTargets.length > 0) {
      const related = await interactionTargetService.getById(
        Number(this.targettypeidInteractionTargets),
      )
      entity.targettypeidInteractionTargets = related.data as InteractionTarget
    }
    return entity
  }

  static async toEntities(data?: LikeFormDTO[] | null) {
    const entities: Promise<Partial<Like>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
