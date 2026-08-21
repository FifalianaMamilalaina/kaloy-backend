import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface ISubmissionStatuse {
  id?: number
  name?: string
}

export class SubmissionStatuse extends BaseModel implements ISubmissionStatuse {
  id?: number
  name?: string

  constructor(data?: Partial<ISubmissionStatuse>) {
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

export class SubmissionStatuseFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<SubmissionStatuseFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<ISubmissionStatuse> | ISubmissionStatuse | null) {
    const instance = new SubmissionStatuseFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: SubmissionStatuse[] | null): SubmissionStatuseFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => SubmissionStatuseFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<SubmissionStatuse>> {
    const entity: Partial<SubmissionStatuse> = new SubmissionStatuse({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: SubmissionStatuseFormDTO[] | null) {
    const entities: Promise<Partial<SubmissionStatuse>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
