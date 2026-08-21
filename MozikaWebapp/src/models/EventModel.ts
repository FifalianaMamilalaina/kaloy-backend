import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'
import { EventModerationStatuse } from './EventModerationStatuseModel'
import * as eventModerationStatuseService from '@/services/EventModerationStatuseService'

export interface IEvent {
  id?: number
  name?: string
  description?: string
  startDate?: string
  endDate?: string
  createdbyartistidArtists?: Artist
  moderationstatusidEventModerationStatuses?: EventModerationStatuse
  reviewedAt?: Date
  createdAt?: Date
}

export class Event extends BaseModel implements IEvent {
  id?: number
  name?: string
  description?: string
  startDate?: string
  endDate?: string
  createdbyartistidArtists?: Artist
  moderationstatusidEventModerationStatuses?: EventModerationStatuse
  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<IEvent>) {
    super()
    this.id = data?.id
    this.name = data?.name
    this.description = data?.description
    this.startDate = data?.startDate
    this.endDate = data?.endDate
    this.createdbyartistidArtists = data?.createdbyartistidArtists
      ? new Artist(data?.createdbyartistidArtists)
      : undefined
    this.moderationstatusidEventModerationStatuses = data?.moderationstatusidEventModerationStatuses
      ? new EventModerationStatuse(data?.moderationstatusidEventModerationStatuses)
      : undefined
    this.reviewedAt = data?.reviewedAt
    this.createdAt = data?.createdAt
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
    {
      key: 'description',
      label: 'Description',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'startDate',
      label: 'Start date',
      type: 'date',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'startDateMin',
      label: 'Start date Min',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'startDateMax',
      label: 'Start date Max',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'endDate',
      label: 'End date',
      type: 'date',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'endDateMin',
      label: 'End date Min',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'endDateMax',
      label: 'End date Max',
      type: 'date',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'createdbyartistidArtists',
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
      key: 'moderationstatusidEventModerationStatuses',
      label: 'Event moderation statuse',
      type: 'select',
      searchKey: EventModerationStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(
        EventModerationStatuse,
        eventModerationStatuseService,
      ),
      multicriteriaSelect: {
        filters: EventModerationStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(eventModerationStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'reviewedAt',
      label: 'Reviewed at',
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

export class EventFormDTO {
  id?: number
  name?: string
  description?: string
  startDate?: string
  endDate?: string
  createdbyartistidArtists?: string

  moderationstatusidEventModerationStatuses?: string

  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<EventFormDTO>) {
    this.id = data?.id
    this.name = data?.name
    this.description = data?.description
    this.startDate = data?.startDate ? data.startDate : undefined

    this.endDate = data?.endDate ? data.endDate : undefined

    this.createdbyartistidArtists = data?.createdbyartistidArtists ?? ''

    this.moderationstatusidEventModerationStatuses =
      data?.moderationstatusidEventModerationStatuses ?? ''

    this.reviewedAt = data?.reviewedAt
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IEvent> | IEvent | null) {
    const instance = new EventFormDTO()
    instance.id = data?.id
    instance.name = data?.name
    instance.description = data?.description
    instance.startDate = data?.startDate
    instance.endDate = data?.endDate
    instance.createdbyartistidArtists = data?.createdbyartistidArtists?.getKeyValue()?.toString()

    instance.moderationstatusidEventModerationStatuses =
      data?.moderationstatusidEventModerationStatuses?.getKeyValue()?.toString()

    instance.reviewedAt = data?.reviewedAt
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Event[] | null): EventFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => EventFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Event>> {
    const entity: Partial<Event> = new Event({
      id: this.id,

      name: this.name,

      description: this.description,

      startDate: this.startDate ? this.startDate.split('T')[0] : undefined,

      endDate: this.endDate ? this.endDate.split('T')[0] : undefined,

      reviewedAt: this.reviewedAt,

      createdAt: this.createdAt,
    })

    if (this.createdbyartistidArtists && this.createdbyartistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.createdbyartistidArtists))
      entity.createdbyartistidArtists = related.data as Artist
    }
    if (
      this.moderationstatusidEventModerationStatuses &&
      this.moderationstatusidEventModerationStatuses.length > 0
    ) {
      const related = await eventModerationStatuseService.getById(
        Number(this.moderationstatusidEventModerationStatuses),
      )
      entity.moderationstatusidEventModerationStatuses = related.data as EventModerationStatuse
    }
    return entity
  }

  static async toEntities(data?: EventFormDTO[] | null) {
    const entities: Promise<Partial<Event>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
