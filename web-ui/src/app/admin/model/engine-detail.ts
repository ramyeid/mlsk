import { ProcessDetail } from './process-detail';
import { RequestDetail } from './request-detail';

export class EngineDetail {

  readonly processesDetails: ProcessDetail[];
  readonly inflightRequestsDetails: RequestDetail[];

  constructor(processesDetails: ProcessDetail[], inflightRequestsDetails: RequestDetail[]) {
    this.processesDetails = processesDetails;
    this.inflightRequestsDetails = inflightRequestsDetails;
  }
}
