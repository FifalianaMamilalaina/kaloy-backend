import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IReportStatuse {
  id?: number
  name?: string
}

export class ReportStatuse extends BaseModel implements IReportStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<IReportStatuse>) {
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

export class ReportStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<ReportStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IReportStatuse> | IReportStatuse | null) {
    const instance = new ReportStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: ReportStatuse[] | null): ReportStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ReportStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ReportStatuse>> {
    const entity: Partial<ReportStatuse> = new ReportStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: ReportStatuseFormDTO[] | null) {
    const entities: Promise<Partial<ReportStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
