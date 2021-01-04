import { TimeSeriesRow } from './time-series-row';

export class TimeSeries {
  private readonly rows: TimeSeriesRow[];
  private readonly dateColumnName: string;
  private readonly valueColumnName: string;
  private readonly dateFormat: string;

  constructor(rows: TimeSeriesRow[], dateColumnName: string, valueColumnName: string, dateFormat: string) {
    this.rows = rows;
    this.dateColumnName = dateColumnName;
    this.valueColumnName = valueColumnName;
    this.dateFormat = dateFormat;
  }
}
