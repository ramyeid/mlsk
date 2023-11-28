export class ProcessDetail {
  readonly id: number;
  readonly state: string;
  readonly flipFlopCount: number;
  readonly startDatetime: string;

  constructor(id: number, state: string, flipFlopCount: number, startDatetime: string) {
    this.id = id;
    this.state = state;
    this.flipFlopCount = flipFlopCount;
    this.startDatetime = startDatetime;
  }

}
