import { TimeSeries } from './time-series';

export class TimeSeriesAnalysisRequest {

  readonly numberOfValues: number;
  readonly timeSeries: TimeSeries;

  constructor(numberOfValues: number, timeSeries: TimeSeries) {
    this.numberOfValues = numberOfValues;
    this.timeSeries = timeSeries;
  }
}
