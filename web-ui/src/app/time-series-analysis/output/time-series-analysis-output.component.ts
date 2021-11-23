import { Component } from '@angular/core';

import { ChartOptions } from 'src/app/shared/chart/chart-options';
import { ChartCoordinate, ChartLine, ChartLines , LineHelper } from 'src/app/shared/chart/line-helper';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesEmittedType } from '../model/time-series-emitted-type';

@Component({
  selector: 'mlsk-time-series-analysis-output',
  templateUrl: './time-series-analysis-output.component.html',
  styleUrls: ['./time-series-analysis-output.component.css']
})
export class TimeSeriesAnalysisOutputComponent {

  forecastAccuracy: string | undefined;
  chartOptions: ChartOptions | undefined;
  chartLines: ChartLines = [];
  shouldDisplay = false;

  onNewRequest(): void {
    this.forecastAccuracy = undefined;
    this.chartOptions = undefined;
    this.chartLines = [];
    this.shouldDisplay = false;
  }

  onResult(resultAndType: [TimeSeries | number, TimeSeriesEmittedType]): void {
    const result: TimeSeries | number = resultAndType[0];
    const timeSeriesEmittedType: TimeSeriesEmittedType = resultAndType[1];

    if (timeSeriesEmittedType == TimeSeriesEmittedType.REQUEST && typeof(result) != 'number') {
      this.onTimeSeriesRequest(result);
    } else {
      if (typeof(result) === 'number') {
        this.onForecastAccuracyResult(result);
      } else {
        this.onTimeSeriesResult(result);
      }
    }
  }

  private onTimeSeriesRequest(request: TimeSeries): void {
    const newLine: ChartLine = this.buildLine(TimeSeriesEmittedType.REQUEST, request);

    this.chartOptions = ChartOptions.buildDefaultChartOptions(request.dateColumnName, request.valueColumnName);

    this.chartLines = [...this.chartLines, newLine];
  }

  private onForecastAccuracyResult(result: number): void {
    this.forecastAccuracy = String(result);
  }

  private onTimeSeriesResult(result: TimeSeries): void {
    const newLine: ChartLine = this.buildLine(TimeSeriesEmittedType.RESULT, result);
    const newConnectedLine: ChartLine = LineHelper.connectLines(this.chartLines[0], newLine);

    this.shouldDisplay = true;

    this.chartLines = [...this.chartLines, newConnectedLine];
  }

  private buildLine(timeSeriesEmittedType: TimeSeriesEmittedType, timeSeries: TimeSeries): ChartLine {
    const lineName: string = timeSeriesEmittedType === TimeSeriesEmittedType.REQUEST ? 'Data' : 'Result';
    const points: ChartCoordinate[] = timeSeries.rows.map(row => LineHelper.buildCoordinate(new Date(row.date), row.value));
    return LineHelper.buildLine(lineName, points);
  }
}
