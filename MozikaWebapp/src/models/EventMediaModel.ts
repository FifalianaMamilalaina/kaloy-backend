import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Event } from './EventModel'
import * as eventService from '@/services/EventService'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { MediaType } from './MediaTypeModel'
import * as mediaTypeService from '@/services/MediaTypeService'

export interface IEventMedia {
  id?: number
  eventidEvents?: Event
  uploaderuseridUsers?: User
  mediatypeidMediaTypes?: MediaType
  url?: string
  createdAt?: Date
}

export class EventMedia extends BaseModel implements IEventMedia {
  id?: number
  eventidEvents?: Event
  uploaderuseridUsers?: User
  mediatypeidMediaTypes?: MediaType
  url?: string
  createdAt?: Date

  constructor(data?: Partial<IEventMedia>) {
    super()
    this.id = data?.id
    this.eventidEvents = data?.eventidEvents ? new Event(data?.eventidEvents) : undefined
    this.uploaderuseridUsers = data?.uploaderuseridUsers
      ? new User(data?.uploaderuseridUsers)
      : undefined
    this.mediatypeidMediaTypes = data?.mediatypeidMediaTypes
      ? new MediaType(data?.mediatypeidMediaTypes)
      : undefined
    this.url = data?.url
    this.createdAt = data?.createdAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'url'
  }

  public override getReferenceValue(): string {
    return String(this.url)
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
      key: 'uploaderuseridUsers',
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
      key: 'mediatypeidMediaTypes',
      label: 'Media type',
      type: 'select',
      searchKey: MediaType.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(MediaType, mediaTypeService),
      multicriteriaSelect: {
        filters: MediaType.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(mediaTypeService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'url',
      label: 'Url',
      type: 'text',
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

export class EventMediaFormDTO {
  id?: number
  eventidEvents?: string

  uploaderuseridUsers?: string

  mediatypeidMediaTypes?: string

  url?: string
  createdAt?: Date

  constructor(data?: Partial<EventMediaFormDTO>) {
    this.id = data?.id
    this.eventidEvents = data?.eventidEvents ?? ''

    this.uploaderuseridUsers = data?.uploaderuseridUsers ?? ''

    this.mediatypeidMediaTypes = data?.mediatypeidMediaTypes ?? ''

    this.url = data?.url
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IEventMedia> | IEventMedia | null) {
    const instance = new EventMediaFormDTO()
    instance.id = data?.id
    instance.eventidEvents = data?.eventidEvents?.getKeyValue()?.toString()

    instance.uploaderuseridUsers = data?.uploaderuseridUsers?.getKeyValue()?.toString()

    instance.mediatypeidMediaTypes = data?.mediatypeidMediaTypes?.getKeyValue()?.toString()

    instance.url = data?.url
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: EventMedia[] | null): EventMediaFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => EventMediaFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<EventMedia>> {
    const entity: Partial<EventMedia> = new EventMedia({
      id: this.id,

      url: this.url,

      createdAt: this.createdAt,
    })

    if (this.eventidEvents && this.eventidEvents.length > 0) {
      const related = await eventService.getById(Number(this.eventidEvents))
      entity.eventidEvents = related.data as Event
    }
    if (this.uploaderuseridUsers && this.uploaderuseridUsers.length > 0) {
      const related = await userService.getById(Number(this.uploaderuseridUsers))
      entity.uploaderuseridUsers = related.data as User
    }
    if (this.mediatypeidMediaTypes && this.mediatypeidMediaTypes.length > 0) {
      const related = await mediaTypeService.getById(Number(this.mediatypeidMediaTypes))
      entity.mediatypeidMediaTypes = related.data as MediaType
    }
    return entity
  }

  static async toEntities(data?: EventMediaFormDTO[] | null) {
    const entities: Promise<Partial<EventMedia>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
