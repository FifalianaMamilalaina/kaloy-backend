import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IParticipationStatuse {
  id?: number
  name?: string
}

export class ParticipationStatuse extends BaseModel implements IParticipationStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IParticipationStatuse>) {
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

export class ParticipationStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<ParticipationStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IParticipationStatuse> | IParticipationStatuse | null) {
    const instance = new ParticipationStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: ParticipationStatuse[] | null): ParticipationStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ParticipationStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ParticipationStatuse>> {
    const entity: Partial<ParticipationStatuse> = new ParticipationStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: ParticipationStatuseFormDTO[] | null) {
    const entities: Promise<Partial<ParticipationStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
