export class ClassifierResponse {

  readonly requestId: number;
  readonly columnName: string;
  readonly values: number[];

  constructor(requestId: number, columnName: string, values: number[]) {
    this.requestId = requestId;
    this.columnName = columnName;
    this.values = values;
  }
}
