import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IVerificationStatuse {
  id?: number
  name?: string
}

export class VerificationStatuse extends BaseModel implements IVerificationStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IVerificationStatuse>) {
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

export class VerificationStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<VerificationStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IVerificationStatuse> | IVerificationStatuse | null) {
    const instance = new VerificationStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: VerificationStatuse[] | null): VerificationStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => VerificationStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<VerificationStatuse>> {
    const entity: Partial<VerificationStatuse> = new VerificationStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: VerificationStatuseFormDTO[] | null) {
    const entities: Promise<Partial<VerificationStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
