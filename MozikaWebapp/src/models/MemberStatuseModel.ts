import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IMemberStatuse {
  id?: number
  name?: string
}

export class MemberStatuse extends BaseModel implements IMemberStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IMemberStatuse>) {
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

export class MemberStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<MemberStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IMemberStatuse> | IMemberStatuse | null) {
    const instance = new MemberStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: MemberStatuse[] | null): MemberStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => MemberStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<MemberStatuse>> {
    const entity: Partial<MemberStatuse> = new MemberStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: MemberStatuseFormDTO[] | null) {
    const entities: Promise<Partial<MemberStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
