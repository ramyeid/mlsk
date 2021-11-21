export class ClassifierStartRequest {

  readonly predictionColumnName: string;
  readonly actionColumnNames: string[];
  readonly numberOfValues: number;

  constructor(predictionColumnName: string, actionColumnNames: string[], numberOfValues: number) {
    this.predictionColumnName = predictionColumnName;
    this.actionColumnNames = actionColumnNames;
    this.numberOfValues = numberOfValues;
  }
}
