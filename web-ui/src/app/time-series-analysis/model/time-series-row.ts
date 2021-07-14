export class TimeSeriesRow {

  readonly date: string;
  readonly value: number;

  constructor(date: string, value: number) {
    this.date = date;
    this.value = value;
  }

}
