import { TimeSeries } from './time-series';

export class TimeSeriesAnalysisRequest {
  private readonly numberOfValues: number;
  private readonly timeSeries: TimeSeries;

  constructor(numberOfValues: number, timeSeries: TimeSeries) {
    this.numberOfValues = numberOfValues;
    this.timeSeries = timeSeries;
  }
}
