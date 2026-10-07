import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Event } from './EventModel'
import * as eventService from '@/services/EventService'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'
import { Venue } from './VenueModel'
import * as venueService from '@/services/VenueService'
import { ParticipationStatuse } from './ParticipationStatuseModel'
import * as participationStatuseService from '@/services/ParticipationStatuseService'
import { EventModerationStatuse } from './EventModerationStatuseModel'
import * as eventModerationStatuseService from '@/services/EventModerationStatuseService'

export interface IConcert {
  id?: number
  eventidEvents?: Event
  title?: string
  description?: string
  artistidArtists?: Artist
  venueidVenues?: Venue
  startTime?: Date
  endTime?: Date
  statusidParticipationStatuses?: ParticipationStatuse
  respondedAt?: Date
  createdbyartistidArtists?: Artist
  moderationstatusidEventModerationStatuses?: EventModerationStatuse
  reviewedAt?: Date
  createdAt?: Date
}

export class Concert extends BaseModel implements IConcert {
  id?: number
  eventidEvents?: Event
  title?: string
  description?: string
  artistidArtists?: Artist
  venueidVenues?: Venue
  startTime?: Date
  endTime?: Date
  statusidParticipationStatuses?: ParticipationStatuse
  respondedAt?: Date
  createdbyartistidArtists?: Artist
  moderationstatusidEventModerationStatuses?: EventModerationStatuse
  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<IConcert>) {
    super()
    this.id = data?.id
    this.eventidEvents = data?.eventidEvents ? new Event(data?.eventidEvents) : undefined
    this.title = data?.title
    this.description = data?.description
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.venueidVenues = data?.venueidVenues ? new Venue(data?.venueidVenues) : undefined
    this.startTime = data?.startTime
    this.endTime = data?.endTime
    this.statusidParticipationStatuses = data?.statusidParticipationStatuses
      ? new ParticipationStatuse(data?.statusidParticipationStatuses)
      : undefined
    this.respondedAt = data?.respondedAt
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
    return 'title'
  }

  public override getReferenceValue(): string {
    return String(this.title)
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
      key: 'eventidEvents',
      label: 'Event',
      type: 'select',
      searchKey: Event.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Event, eventService),
      multicriteriaSelect: {
        filters: Event.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(eventService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'title',
      label: 'Title',
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
      key: 'artistidArtists',
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
      key: 'venueidVenues',
      label: 'Venue',
      type: 'select',
      searchKey: Venue.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(Venue, venueService),
      multicriteriaSelect: {
        filters: Venue.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(venueService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'startTime',
      label: 'Start time',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'endTime',
      label: 'End time',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'statusidParticipationStatuses',
      label: 'Participation statuse',
      type: 'select',
      searchKey: ParticipationStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(ParticipationStatuse, participationStatuseService),
      multicriteriaSelect: {
        filters: ParticipationStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(participationStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'respondedAt',
      label: 'Responded at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
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

export class ConcertFormDTO {
  id?: number
  eventidEvents?: string

  title?: string
  description?: string
  artistidArtists?: string

  venueidVenues?: string

  startTime?: Date
  endTime?: Date
  statusidParticipationStatuses?: string

  respondedAt?: Date
  createdbyartistidArtists?: string

  moderationstatusidEventModerationStatuses?: string

  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<ConcertFormDTO>) {
    this.id = data?.id
    this.eventidEvents = data?.eventidEvents ?? ''

    this.title = data?.title
    this.description = data?.description
    this.artistidArtists = data?.artistidArtists ?? ''

    this.venueidVenues = data?.venueidVenues ?? ''

    this.startTime = data?.startTime
    this.endTime = data?.endTime
    this.statusidParticipationStatuses = data?.statusidParticipationStatuses ?? ''

    this.respondedAt = data?.respondedAt
    this.createdbyartistidArtists = data?.createdbyartistidArtists ?? ''

    this.moderationstatusidEventModerationStatuses =
      data?.moderationstatusidEventModerationStatuses ?? ''

    this.reviewedAt = data?.reviewedAt
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IConcert> | IConcert | null) {
    const instance = new ConcertFormDTO()
    instance.id = data?.id
    instance.eventidEvents = data?.eventidEvents?.getKeyValue()?.toString()

    instance.title = data?.title
    instance.description = data?.description
    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.venueidVenues = data?.venueidVenues?.getKeyValue()?.toString()

    instance.startTime = data?.startTime
    instance.endTime = data?.endTime
    instance.statusidParticipationStatuses = data?.statusidParticipationStatuses
      ?.getKeyValue()
      ?.toString()

    instance.respondedAt = data?.respondedAt
    instance.createdbyartistidArtists = data?.createdbyartistidArtists?.getKeyValue()?.toString()

    instance.moderationstatusidEventModerationStatuses =
      data?.moderationstatusidEventModerationStatuses?.getKeyValue()?.toString()

    instance.reviewedAt = data?.reviewedAt
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Concert[] | null): ConcertFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ConcertFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Concert>> {
    const entity: Partial<Concert> = new Concert({
      id: this.id,

      title: this.title,

      description: this.description,

      startTime: this.startTime,

      endTime: this.endTime,

      respondedAt: this.respondedAt,

      reviewedAt: this.reviewedAt,

      createdAt: this.createdAt,
    })

    if (this.eventidEvents && this.eventidEvents.length > 0) {
      const related = await eventService.getById(Number(this.eventidEvents))
      entity.eventidEvents = related.data as Event
    }
    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    if (this.venueidVenues && this.venueidVenues.length > 0) {
      const related = await venueService.getById(Number(this.venueidVenues))
      entity.venueidVenues = related.data as Venue
    }
    if (this.statusidParticipationStatuses && this.statusidParticipationStatuses.length > 0) {
      const related = await participationStatuseService.getById(
        Number(this.statusidParticipationStatuses),
      )
      entity.statusidParticipationStatuses = related.data as ParticipationStatuse
    }
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

  static async toEntities(data?: ConcertFormDTO[] | null) {
    const entities: Promise<Partial<Concert>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
