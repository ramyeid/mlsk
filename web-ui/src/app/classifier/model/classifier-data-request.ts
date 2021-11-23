export class ClassifierDataRequest {

  readonly columnName: string;
  readonly values: number[];
  readonly requestId: string;

  constructor(columnName: string, values: number[], requestId: string) {
    this.columnName = columnName;
    this.values = values;
    this.requestId = requestId;
  }
}
