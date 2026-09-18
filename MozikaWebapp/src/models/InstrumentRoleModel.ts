import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IInstrumentRole {
  id?: number
  label?: string
}

export class InstrumentRole extends BaseModel implements IInstrumentRole {
  id?: number
  label?: string

  constructor(data?: Partial<IInstrumentRole>) {
    super()
    this.id = data?.id
    this.label = data?.label
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'label'
  }

  public override getReferenceValue(): string {
    return String(this.label)
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
      key: 'label',
      label: 'Label',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class InstrumentRoleFormDTO {
  id?: number
  label?: string

  constructor(data?: Partial<InstrumentRoleFormDTO>) {
    this.id = data?.id
    this.label = data?.label
  }

  static parse(data?: Partial<IInstrumentRole> | IInstrumentRole | null) {
    const instance = new InstrumentRoleFormDTO()
    instance.id = data?.id
    instance.label = data?.label
    return instance
  }

  static parseList(data?: InstrumentRole[] | null): InstrumentRoleFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => InstrumentRoleFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<InstrumentRole>> {
    const entity: Partial<InstrumentRole> = new InstrumentRole({
      id: this.id,

      label: this.label,
    })

    return entity
  }

  static async toEntities(data?: InstrumentRoleFormDTO[] | null) {
    const entities: Promise<Partial<InstrumentRole>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
