import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'
import { InstrumentRole } from './InstrumentRoleModel'
import * as instrumentRoleService from '@/services/InstrumentRoleService'
import { MemberStatuse } from './MemberStatuseModel'
import * as memberStatuseService from '@/services/MemberStatuseService'

export interface IArtistGroupMember {
  id?: number
  groupartistidArtists?: Artist
  memberartistidArtists?: Artist
  fullName?: string
  roleinstrumentidInstrumentRoles?: InstrumentRole
  photoUrl?: string
  statusidMemberStatuses?: MemberStatuse
  createdAt?: Date
}

export class ArtistGroupMember extends BaseModel implements IArtistGroupMember {
  id?: number
  groupartistidArtists?: Artist
  memberartistidArtists?: Artist
  fullName?: string
  roleinstrumentidInstrumentRoles?: InstrumentRole
  photoUrl?: string
  statusidMemberStatuses?: MemberStatuse
  createdAt?: Date

  constructor(data?: Partial<IArtistGroupMember>) {
    super()
    this.id = data?.id
    this.groupartistidArtists = data?.groupartistidArtists
      ? new Artist(data?.groupartistidArtists)
      : undefined
    this.memberartistidArtists = data?.memberartistidArtists
      ? new Artist(data?.memberartistidArtists)
      : undefined
    this.fullName = data?.fullName
    this.roleinstrumentidInstrumentRoles = data?.roleinstrumentidInstrumentRoles
      ? new InstrumentRole(data?.roleinstrumentidInstrumentRoles)
      : undefined
    this.photoUrl = data?.photoUrl
    this.statusidMemberStatuses = data?.statusidMemberStatuses
      ? new MemberStatuse(data?.statusidMemberStatuses)
      : undefined
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'fullName'
  }

  public override getReferenceValue(): string {
    return String(this.fullName)
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
      key: 'groupartistidArtists',
      label: 'Artist',
      type: 'select',
      searchKey: Artist.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Artist, artistService),
      multicriteriaSelect: {
        filters: Artist.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(artistService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'memberartistidArtists',
      label: 'Artist',
      type: 'select',
      searchKey: Artist.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Artist, artistService),
      multicriteriaSelect: {
        filters: Artist.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(artistService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'fullName',
      label: 'Full name',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'roleinstrumentidInstrumentRoles',
      label: 'Instrument role',
      type: 'select',
      searchKey: InstrumentRole.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(InstrumentRole, instrumentRoleService),
      multicriteriaSelect: {
        filters: InstrumentRole.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(instrumentRoleService),
      },
      showInTable: true,
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
      key: 'statusidMemberStatuses',
      label: 'Member statuse',
      type: 'select',
      searchKey: MemberStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(MemberStatuse, memberStatuseService),
      multicriteriaSelect: {
        filters: MemberStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(memberStatuseService),
      },
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

export class ArtistGroupMemberFormDTO {
  id?: number
  groupartistidArtists?: string

  memberartistidArtists?: string

  fullName?: string
  roleinstrumentidInstrumentRoles?: string

  photoUrl?: string
  statusidMemberStatuses?: string

  createdAt?: Date

  constructor(data?: Partial<ArtistGroupMemberFormDTO>) {
    this.id = data?.id
    this.groupartistidArtists = data?.groupartistidArtists ?? ''

    this.memberartistidArtists = data?.memberartistidArtists ?? ''

    this.fullName = data?.fullName
    this.roleinstrumentidInstrumentRoles = data?.roleinstrumentidInstrumentRoles ?? ''

    this.photoUrl = data?.photoUrl
    this.statusidMemberStatuses = data?.statusidMemberStatuses ?? ''

    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IArtistGroupMember> | IArtistGroupMember | null) {
    const instance = new ArtistGroupMemberFormDTO()
    instance.id = data?.id
    instance.groupartistidArtists = data?.groupartistidArtists?.getKeyValue()?.toString()

    instance.memberartistidArtists = data?.memberartistidArtists?.getKeyValue()?.toString()

    instance.fullName = data?.fullName
    instance.roleinstrumentidInstrumentRoles = data?.roleinstrumentidInstrumentRoles
      ?.getKeyValue()
      ?.toString()

    instance.photoUrl = data?.photoUrl
    instance.statusidMemberStatuses = data?.statusidMemberStatuses?.getKeyValue()?.toString()

    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: ArtistGroupMember[] | null): ArtistGroupMemberFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ArtistGroupMemberFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ArtistGroupMember>> {
    const entity: Partial<ArtistGroupMember> = new ArtistGroupMember({
      id: this.id,

      fullName: this.fullName,

      photoUrl: this.photoUrl,

      createdAt: this.createdAt,
    })

    if (this.groupartistidArtists && this.groupartistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.groupartistidArtists))
      entity.groupartistidArtists = related.data as Artist
    }
    if (this.memberartistidArtists && this.memberartistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.memberartistidArtists))
      entity.memberartistidArtists = related.data as Artist
    }
    if (this.roleinstrumentidInstrumentRoles && this.roleinstrumentidInstrumentRoles.length > 0) {
      const related = await instrumentRoleService.getById(
        Number(this.roleinstrumentidInstrumentRoles),
      )
      entity.roleinstrumentidInstrumentRoles = related.data as InstrumentRole
    }
    if (this.statusidMemberStatuses && this.statusidMemberStatuses.length > 0) {
      const related = await memberStatuseService.getById(Number(this.statusidMemberStatuses))
      entity.statusidMemberStatuses = related.data as MemberStatuse
    }
    return entity
  }

  static async toEntities(data?: ArtistGroupMemberFormDTO[] | null) {
    const entities: Promise<Partial<ArtistGroupMember>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
