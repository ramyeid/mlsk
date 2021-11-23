export class ClassifierDataResponse {

  readonly columnName: string;
  readonly values: number[];

  constructor(columnName: string, values: number[]) {
    this.columnName = columnName;
    this.values = values;
  }
}
