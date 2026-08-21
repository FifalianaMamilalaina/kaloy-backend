import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { Artist } from './ArtistModel'
import * as artistService from '@/services/ArtistService'
import { SubmissionStatuse } from './SubmissionStatuseModel'
import * as submissionStatuseService from '@/services/SubmissionStatuseService'

export interface IContentSubmission {
  id?: number
  artistidArtists?: Artist
  sourceFilename?: string
  fileType?: string
  statusidSubmissionStatuses?: SubmissionStatuse
  errorMessage?: string
  submittedAt?: Date
  processedAt?: Date
}

export class ContentSubmission extends BaseModel implements IContentSubmission {
  id?: number
  artistidArtists?: Artist
  sourceFilename?: string
  fileType?: string
  statusidSubmissionStatuses?: SubmissionStatuse
  errorMessage?: string
  submittedAt?: Date
  processedAt?: Date

  constructor(data?: Partial<IContentSubmission>) {
    super()
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ? new Artist(data?.artistidArtists) : undefined
    this.sourceFilename = data?.sourceFilename
    this.fileType = data?.fileType
    this.statusidSubmissionStatuses = data?.statusidSubmissionStatuses
      ? new SubmissionStatuse(data?.statusidSubmissionStatuses)
      : undefined
    this.errorMessage = data?.errorMessage
    this.submittedAt = data?.submittedAt
    this.processedAt = data?.processedAt
  }

  static override getKey(): string {
    return 'id'
  }

  public override getKeyValue(): string {
    return String(this.id)
  }

  static override getReferenceKey(): string {
    return 'sourceFilename'
  }

  public override getReferenceValue(): string {
    return String(this.sourceFilename)
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
      key: 'sourceFilename',
      label: 'Source filename',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'fileType',
      label: 'File type',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'statusidSubmissionStatuses',
      label: 'Submission statuse',
      type: 'select',
      searchKey: SubmissionStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(SubmissionStatuse, submissionStatuseService),
      multicriteriaSelect: {
        filters: SubmissionStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(submissionStatuseService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'errorMessage',
      label: 'Error message',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'submittedAt',
      label: 'Submitted at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'processedAt',
      label: 'Processed at',
      type: 'datetime-local',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
  ]
}

export class ContentSubmissionFormDTO {
  id?: number
  artistidArtists?: string

  sourceFilename?: string
  fileType?: string
  statusidSubmissionStatuses?: string

  errorMessage?: string
  submittedAt?: Date
  processedAt?: Date

  constructor(data?: Partial<ContentSubmissionFormDTO>) {
    this.id = data?.id
    this.artistidArtists = data?.artistidArtists ?? ''

    this.sourceFilename = data?.sourceFilename
    this.fileType = data?.fileType
    this.statusidSubmissionStatuses = data?.statusidSubmissionStatuses ?? ''

    this.errorMessage = data?.errorMessage
    this.submittedAt = data?.submittedAt
    this.processedAt = data?.processedAt
  }

  static parse(data?: Partial<IContentSubmission> | IContentSubmission | null) {
    const instance = new ContentSubmissionFormDTO()
    instance.id = data?.id
    instance.artistidArtists = data?.artistidArtists?.getKeyValue()?.toString()

    instance.sourceFilename = data?.sourceFilename
    instance.fileType = data?.fileType
    instance.statusidSubmissionStatuses = data?.statusidSubmissionStatuses
      ?.getKeyValue()
      ?.toString()

    instance.errorMessage = data?.errorMessage
    instance.submittedAt = data?.submittedAt
    instance.processedAt = data?.processedAt
    return instance
  }

  static parseList(data?: ContentSubmission[] | null): ContentSubmissionFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ContentSubmissionFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<ContentSubmission>> {
    const entity: Partial<ContentSubmission> = new ContentSubmission({
      id: this.id,

      sourceFilename: this.sourceFilename,

      fileType: this.fileType,

      errorMessage: this.errorMessage,

      submittedAt: this.submittedAt,

      processedAt: this.processedAt,
    })

    if (this.artistidArtists && this.artistidArtists.length > 0) {
      const related = await artistService.getById(Number(this.artistidArtists))
      entity.artistidArtists = related.data as Artist
    }
    if (this.statusidSubmissionStatuses && this.statusidSubmissionStatuses.length > 0) {
      const related = await submissionStatuseService.getById(
        Number(this.statusidSubmissionStatuses),
      )
      entity.statusidSubmissionStatuses = related.data as SubmissionStatuse
    }
    return entity
  }

  static async toEntities(data?: ContentSubmissionFormDTO[] | null) {
    const entities: Promise<Partial<ContentSubmission>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
