import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'

export interface IVerificationChannel {
  id?: number
  name?: string
}

export class VerificationChannel extends BaseModel implements IVerificationChannel {
  id?: number
  name?: string

  constructor(data?: Partial<IVerificationChannel>) {
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

export class VerificationChannelFormDTO {
  id?: number
  name?: string

  constructor(data?: Partial<VerificationChannelFormDTO>) {
    this.id = data?.id
    this.name = data?.name
  }

  static parse(data?: Partial<IVerificationChannel> | IVerificationChannel | null) {
    const instance = new VerificationChannelFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    return instance
  }

  static parseList(data?: VerificationChannel[] | null): VerificationChannelFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => VerificationChannelFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<VerificationChannel>> {
    const entity: Partial<VerificationChannel> = new VerificationChannel({
      id: this.id,

      name: this.name,
    })

    return entity
  }

  static async toEntities(data?: VerificationChannelFormDTO[] | null) {
    const entities: Promise<Partial<VerificationChannel>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
