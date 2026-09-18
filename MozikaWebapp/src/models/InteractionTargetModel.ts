import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IInteractionTarget {
  id?: number
  name?: string
}

export class InteractionTarget extends BaseModel implements IInteractionTarget {
  id?: number
  name?: string

  constructor(data?: Partial<IInteractionTarget>) {
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

export class InteractionTargetFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<InteractionTargetFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IInteractionTarget> | IInteractionTarget | null) {
    const instance = new InteractionTargetFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: InteractionTarget[] | null): InteractionTargetFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => InteractionTargetFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<InteractionTarget>> {
    const entity: Partial<InteractionTarget> = new InteractionTarget({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: InteractionTargetFormDTO[] | null) {
    const entities: Promise<Partial<InteractionTarget>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
