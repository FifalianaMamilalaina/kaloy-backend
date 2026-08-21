import { BaseModel } from './BaseModel'
import { type EntitySearchField } from './EntityModel'
import { createSelectSearchFunction, createMulticriteriatSearchFunction } from './SelectOption'
import { User } from './UserModel'
import * as userService from '@/services/UserService'
import { InteractionTarget } from './InteractionTargetModel'
import * as interactionTargetService from '@/services/InteractionTargetService'
import { ReportStatuse } from './ReportStatuseModel'
import * as reportStatuseService from '@/services/ReportStatuseService'

export interface IReport {
  id?: number
  reporteruseridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  reason?: string
  statusidReportStatuses?: ReportStatuse
  reviewedAt?: Date
  createdAt?: Date
}

export class Report extends BaseModel implements IReport {
  id?: number
  reporteruseridUsers?: User
  targettypeidInteractionTargets?: InteractionTarget
  targetId?: number
  reason?: string
  statusidReportStatuses?: ReportStatuse
  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<IReport>) {
    super()
    this.id = data?.id
    this.reporteruseridUsers = data?.reporteruseridUsers
      ? new User(data?.reporteruseridUsers)
      : undefined
    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ? new InteractionTarget(data?.targettypeidInteractionTargets)
      : undefined
    this.targetId = data?.targetId
    this.reason = data?.reason
    this.statusidReportStatuses = data?.statusidReportStatuses
      ? new ReportStatuse(data?.statusidReportStatuses)
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
    return 'targetId'
  }

  public override getReferenceValue(): string {
    return String(this.targetId)
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
      key: 'reporteruseridUsers',
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
      key: 'targettypeidInteractionTargets',
      label: 'Interaction target',
      type: 'select',
      searchKey: InteractionTarget.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(InteractionTarget, interactionTargetService),
      multicriteriaSelect: {
        filters: InteractionTarget.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(interactionTargetService),
      },
      showInTable: true,
      showInFilter: true,
    },

    {
      key: 'targetId',
      label: 'Target id',
      type: 'number',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'targetIdMin',
      label: 'Target id Min',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },
    {
      key: 'targetIdMax',
      label: 'Target id Max',
      type: 'number',
      sortable: false,
      showInTable: false,
      showInFilter: true,
    },

    {
      key: 'reason',
      label: 'Reason',
      type: 'text',
      sortable: true,
      showInTable: true,
      showInFilter: true,
    },
    {
      key: 'statusidReportStatuses',
      label: 'Report statuse',
      type: 'select',
      searchKey: ReportStatuse.getKey(),
      sortable: false,
      selectSearch: createSelectSearchFunction(ReportStatuse, reportStatuseService),
      multicriteriaSelect: {
        filters: ReportStatuse.getAllSearchFieldsMetadata(),
        searchFunction: createMulticriteriatSearchFunction(reportStatuseService),
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

export class ReportFormDTO {
  id?: number
  reporteruseridUsers?: string

  targettypeidInteractionTargets?: string

  targetId?: number
  reason?: string
  statusidReportStatuses?: string

  reviewedAt?: Date
  createdAt?: Date

  constructor(data?: Partial<ReportFormDTO>) {
    this.id = data?.id
    this.reporteruseridUsers = data?.reporteruseridUsers ?? ''

    this.targettypeidInteractionTargets = data?.targettypeidInteractionTargets ?? ''

    this.targetId = data?.targetId
    this.reason = data?.reason
    this.statusidReportStatuses = data?.statusidReportStatuses ?? ''

    this.reviewedAt = data?.reviewedAt
    this.createdAt = data?.createdAt
  }

  static parse(data?: Partial<IReport> | IReport | null) {
    const instance = new ReportFormDTO()
    instance.id = data?.id
    instance.reporteruseridUsers = data?.reporteruseridUsers?.getKeyValue()?.toString()

    instance.targettypeidInteractionTargets = data?.targettypeidInteractionTargets
      ?.getKeyValue()
      ?.toString()

    instance.targetId = data?.targetId
    instance.reason = data?.reason
    instance.statusidReportStatuses = data?.statusidReportStatuses?.getKeyValue()?.toString()

    instance.reviewedAt = data?.reviewedAt
    instance.createdAt = data?.createdAt
    return instance
  }

  static parseList(data?: Report[] | null): ReportFormDTO[] {
    if (!data || data.length === 0) return []
    return data.map((item) => ReportFormDTO.parse(item))
  }

  async toEntity(): Promise<Partial<Report>> {
    const entity: Partial<Report> = new Report({
      id: this.id,

      targetId: this.targetId,

      reason: this.reason,

      reviewedAt: this.reviewedAt,

      createdAt: this.createdAt,
    })

    if (this.reporteruseridUsers && this.reporteruseridUsers.length > 0) {
      const related = await userService.getById(Number(this.reporteruseridUsers))
      entity.reporteruseridUsers = related.data as User
    }
    if (this.targettypeidInteractionTargets && this.targettypeidInteractionTargets.length > 0) {
      const related = await interactionTargetService.getById(
        Number(this.targettypeidInteractionTargets),
      )
      entity.targettypeidInteractionTargets = related.data as InteractionTarget
    }
    if (this.statusidReportStatuses && this.statusidReportStatuses.length > 0) {
      const related = await reportStatuseService.getById(Number(this.statusidReportStatuses))
      entity.statusidReportStatuses = related.data as ReportStatuse
    }
    return entity
  }

  static async toEntities(data?: ReportFormDTO[] | null) {
    const entities: Promise<Partial<Report>>[] = []
    data?.forEach(async (item) => {
      entities.push(item.toEntity())
    })
    return Promise.all(entities)
  }
}
