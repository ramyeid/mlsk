import { TimeSeriesRow } from './time-series-row';

export class TimeSeries {

  readonly rows: TimeSeriesRow[];
  readonly dateColumnName: string;
  readonly valueColumnName: string;
  readonly dateFormat: string;

  constructor(rows: TimeSeriesRow[], dateColumnName: string, valueColumnName: string, dateFormat: string) {
    this.rows = rows;
    this.dateColumnName = dateColumnName;
    this.valueColumnName = valueColumnName;
    this.dateFormat = dateFormat;
  }
}
