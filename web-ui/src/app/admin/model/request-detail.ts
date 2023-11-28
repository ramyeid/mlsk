export class RequestDetail {
  readonly id: number;
  readonly type: string;
  readonly creationDatetime: string;

  constructor(id: number, type: string, creationDatetime: string) {
    this.id = id;
    this.type = type;
    this.creationDatetime = creationDatetime;
  }

}
