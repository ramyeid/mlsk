import { Component } from '@angular/core';

import { ChartOptions } from 'src/app/shared/chart-options';
import { ChartCoordinate, ChartLine, ChartLines , LineHelper } from 'src/app/shared/line-helper';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesType } from '../model/time-series-type';

@Component({
  selector: 'app-time-series-analysis-output',
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

  onResult(resultAndType: [TimeSeries | number, TimeSeriesType]): void {
    const result: TimeSeries | number = resultAndType[0];
    const timeSeriesType: TimeSeriesType = resultAndType[1];

    if (timeSeriesType == TimeSeriesType.REQUEST && typeof(result) != 'number') {
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
    const newLine: ChartLine = this.buildLine(TimeSeriesType.REQUEST, request);

    this.chartOptions = ChartOptions.buildDefaultChartOptions(request.dateColumnName, request.valueColumnName);

    this.chartLines = [...this.chartLines, newLine];
  }

  private onForecastAccuracyResult(result: number): void {
    this.forecastAccuracy = String(result);
  }

  private onTimeSeriesResult(result: TimeSeries): void {
    const newLine: ChartLine = this.buildLine(TimeSeriesType.RESULT, result);
    const newConnectedLine: ChartLine = LineHelper.connectLines(this.chartLines[0], newLine);

    this.shouldDisplay = true;

    this.chartLines = [...this.chartLines, newConnectedLine];
  }

  private buildLine(timeSeriesType: TimeSeriesType, timeSeries: TimeSeries): ChartLine {
    const lineName: string = timeSeriesType === TimeSeriesType.REQUEST ? 'Data' : 'Result';
    const points: ChartCoordinate[] = timeSeries.rows.map(row => LineHelper.buildCoordinate(new Date(row.date), row.value));
    return LineHelper.buildLine(lineName, points);
  }
}
