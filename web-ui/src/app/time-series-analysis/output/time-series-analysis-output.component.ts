import { Component } from '@angular/core';

import { ChartOptions } from 'src/app/shared/chart/chart-options';
import { ChartCoordinate, ChartLine, ChartLines , LineHelper } from 'src/app/shared/chart/line-helper';
import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { TimeSeries } from '../model/time-series';

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

  onResult(resultAndType: [TimeSeries | number, InputEmitType]): void {
    const result: TimeSeries | number = resultAndType[0];
    const inputEmitType: InputEmitType = resultAndType[1];

    if (inputEmitType == InputEmitType.REQUEST && typeof(result) != 'number') {
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
    const newLine: ChartLine = this.buildLine(InputEmitType.REQUEST, request);

    this.chartOptions = ChartOptions.buildDefaultChartOptions(request.dateColumnName, request.valueColumnName);

    this.chartLines = [...this.chartLines, newLine];
  }

  private onForecastAccuracyResult(result: number): void {
    this.forecastAccuracy = String(result);
  }

  private onTimeSeriesResult(result: TimeSeries): void {
    const newLine: ChartLine = this.buildLine(InputEmitType.RESULT, result);
    const newConnectedLine: ChartLine = LineHelper.connectLines(this.chartLines[0], newLine);

    this.shouldDisplay = true;

    this.chartLines = [...this.chartLines, newConnectedLine];
  }

  private buildLine(inputEmitType: InputEmitType, timeSeries: TimeSeries): ChartLine {
    const lineName: string = inputEmitType === InputEmitType.REQUEST ? 'Data' : 'Result';
    const points: ChartCoordinate[] = timeSeries.rows.map(row => LineHelper.buildCoordinate(new Date(row.date), row.value));
    return LineHelper.buildLine(lineName, points);
  }
}
