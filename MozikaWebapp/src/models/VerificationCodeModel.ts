import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { VerificationChannel } from './VerificationChannelModel'
import * as verificationChannelService from '@/services/VerificationChannelService'

export interface IVerificationCode {
  id?: number
  useridUsers?: User
  channelidVerificationChannels?: VerificationChannel
  destination?: string
  code?: string
  expiresAt?: Date
  consumedAt?: Date
  createdAt?: Date
}

export class VerificationCode extends BaseModel implements IVerificationCode {
  id?: number
  useridUsers?: User
  channelidVerificationChannels?: VerificationChannel
  destination?: string
  code?: string
  expiresAt?: Date
  consumedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<IVerificationCode>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.channelidVerificationChannels = data?.channelidVerificationChannels
      ? new VerificationChannel(data?.channelidVerificationChannels)
      : undefined
    this.destination = data?.destination
    this.code = data?.code
    this.expiresAt = data?.expiresAt
    this.consumedAt = data?.consumedAt
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'destination'
  }

  public override getReferenceValue(): string {
    return String(this.destination)
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
      key: 'channelidVerificationChannels',
      label: 'Verification channel',
      type: 'select',
      searchKey: VerificationChannel.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(VerificationChannel, verificationChannelService),
      multicriteriaSelect: {
        filters: VerificationChannel.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(verificationChannelService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'destination',
      label: 'Destination',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'code',
      label: 'Code',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'expiresAt',
      label: 'Expires at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'consumedAt',
      label: 'Consumed at',
      type: 'datetime-local',
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

export class VerificationCodeFormDTO {
  id?: number
  useridUsers?: string

  channelidVerificationChannels?: string

  destination?: string
  code?: string
  expiresAt?: Date
  consumedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<VerificationCodeFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.channelidVerificationChannels = data?.channelidVerificationChannels ?? ''

    this.destination = data?.destination
    this.code = data?.code
    this.expiresAt = data?.expiresAt
    this.consumedAt = data?.consumedAt
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IVerificationCode> | IVerificationCode | null) {
    const instance = new VerificationCodeFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.channelidVerificationChannels = data?.channelidVerificationChannels
      ?.getKeyValue()
      ?.toString()

    instance.destination = data?.destination
    instance.code = data?.code
    instance.expiresAt = data?.expiresAt
    instance.consumedAt = data?.consumedAt
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: VerificationCode[] | null): VerificationCodeFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => VerificationCodeFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<VerificationCode>> {
    const entity: Partial<VerificationCode> = new VerificationCode({
      id: this.id,

      destination: this.destination,

      code: this.code,

      expiresAt: this.expiresAt,

      consumedAt: this.consumedAt,

      createdAt: this.createdAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.channelidVerificationChannels && this.channelidVerificationChannels.length > 0) {
      const related = await verificationChannelService.getById(
        Number(this.channelidVerificationChannels),
      )
      entity.channelidVerificationChannels = related.data as VerificationChannel
    }
    return entity
  }

  static async toEntities(data?: VerificationCodeFormDTO[] | null) {
    const entities: Promise<Partial<VerificationCode>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
