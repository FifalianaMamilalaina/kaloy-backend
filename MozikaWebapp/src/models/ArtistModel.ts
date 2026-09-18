import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { ArtistType } from './ArtistTypeModel'
import * as artistTypeService from '@/services/ArtistTypeService'
import { VerificationStatuse } from './VerificationStatuseModel'
import * as verificationStatuseService from '@/services/VerificationStatuseService'

export interface IArtist {
  id?: number
  useridUsers?: User
  artisttypeidArtistTypes?: ArtistType
  stageName?: string
  activeSinceYear?: number
  photoUrl?: string
  bio?: string
  verificationstatusidVerificationStatuses?: VerificationStatuse
  verifiedAt?: Date
  isCertified?: boolean
  createdAt?: Date
}

export class Artist extends BaseModel implements IArtist {
  id?: number
  useridUsers?: User
  artisttypeidArtistTypes?: ArtistType
  stageName?: string
  activeSinceYear?: number
  photoUrl?: string
  bio?: string
  verificationstatusidVerificationStatuses?: VerificationStatuse
  verifiedAt?: Date
  isCertified?: boolean
  createdAt?: Date

  constructor(data?: Partial<IArtist>) {
    super()
    this.id = data?.id
    this.useridUsers = data?.useridUsers ? new User(data?.useridUsers) : undefined
    this.artisttypeidArtistTypes = data?.artisttypeidArtistTypes
      ? new ArtistType(data?.artisttypeidArtistTypes)
      : undefined
    this.stageName = data?.stageName
    this.activeSinceYear = data?.activeSinceYear
    this.photoUrl = data?.photoUrl
    this.bio = data?.bio
    this.verificationstatusidVerificationStatuses = data?.verificationstatusidVerificationStatuses
      ? new VerificationStatuse(data?.verificationstatusidVerificationStatuses)
      : undefined
    this.verifiedAt = data?.verifiedAt
    this.isCertified = data?.isCertified
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'stageName'
  }

  public override getReferenceValue(): string {
    return String(this.stageName)
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
      key: 'artisttypeidArtistTypes',
      label: 'Artist type',
      type: 'select',
      searchKey: ArtistType.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(ArtistType, artistTypeService),
      multicriteriaSelect: {
        filters: ArtistType.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(artistTypeService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'stageName',
      label: 'Stage name',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'activeSinceYear',
      label: 'Active since year',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'activeSinceYearMin',
      label: 'Active since year Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'activeSinceYearMax',
      label: 'Active since year Max',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'photoUrl',
      label: 'Photo url',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'bio',
      label: 'Bio',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'verificationstatusidVerificationStatuses',
      label: 'Verification statuse',
      type: 'select',
      searchKey: VerificationStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(VerificationStatuse, verificationStatuseService),
      multicriteriaSelect: {
        filters: VerificationStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(verificationStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'verifiedAt',
      label: 'Verified at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'isCertified',
      label: 'Is certified',
      type: 'checkbox',
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

export class ArtistFormDTO {
  id?: number
  useridUsers?: string

  artisttypeidArtistTypes?: string

  stageName?: string
  activeSinceYear?: number
  photoUrl?: string
  bio?: string
  verificationstatusidVerificationStatuses?: string

  verifiedAt?: Date
  isCertified?: boolean
  createdAt?: Date

  constructor(data?: Partial<ArtistFormDTO>) {
    this.id = data?.id
    this.useridUsers = data?.useridUsers ?? ''

    this.artisttypeidArtistTypes = data?.artisttypeidArtistTypes ?? ''

    this.stageName = data?.stageName
    this.activeSinceYear = data?.activeSinceYear
    this.photoUrl = data?.photoUrl
    this.bio = data?.bio
    this.verificationstatusidVerificationStatuses =
      data?.verificationstatusidVerificationStatuses ?? ''

    this.verifiedAt = data?.verifiedAt
    this.isCertified = data?.isCertified
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IArtist> | IArtist | null) {
    const instance = new ArtistFormDTO()
    instance.id = data?.id
    instance.useridUsers = data?.useridUsers?.getKeyValue()?.toString()

    instance.artisttypeidArtistTypes = data?.artisttypeidArtistTypes?.getKeyValue()?.toString()

    instance.stageName = data?.stageName
    instance.activeSinceYear = data?.activeSinceYear
    instance.photoUrl = data?.photoUrl
    instance.bio = data?.bio
    instance.verificationstatusidVerificationStatuses =
      data?.verificationstatusidVerificationStatuses?.getKeyValue()?.toString()

    instance.verifiedAt = data?.verifiedAt
    instance.isCertified = data?.isCertified
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Artist[] | null): ArtistFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ArtistFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Artist>> {
    const entity: Partial<Artist> = new Artist({
      id: this.id,

      stageName: this.stageName,

      activeSinceYear: this.activeSinceYear,

      photoUrl: this.photoUrl,

      bio: this.bio,

      verifiedAt: this.verifiedAt,

      isCertified: this.isCertified,

      createdAt: this.createdAt,
    })

    if (this.useridUsers && this.useridUsers.length > 0) {
      const related = await userService.getById(Number(this.useridUsers))
      entity.useridUsers = related.data as User
    }
    if (this.artisttypeidArtistTypes && this.artisttypeidArtistTypes.length > 0) {
      const related = await artistTypeService.getById(Number(this.artisttypeidArtistTypes))
      entity.artisttypeidArtistTypes = related.data as ArtistType
    }
    if (
      this.verificationstatusidVerificationStatuses &&
      this.verificationstatusidVerificationStatuses.length > 0
    ) {
      const related = await verificationStatuseService.getById(
        Number(this.verificationstatusidVerificationStatuses),
      )
      entity.verificationstatusidVerificationStatuses = related.data as VerificationStatuse
    }
    return entity
  }

  static async toEntities(data?: ArtistFormDTO[] | null) {
    const entities: Promise<Partial<Artist>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
