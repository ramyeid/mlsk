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

  chartOptions: ChartOptions | undefined;
  chartLines: ChartLines = [];
  shouldDisplay = false;

  onNewRequest(): void {
    this.chartOptions = undefined;
    this.chartLines = [];
    this.shouldDisplay = false;
  }

  onTimeSeriesResult(timeSeriesAndType: [TimeSeries, TimeSeriesType]): void {
    const timeSeries: TimeSeries = timeSeriesAndType[0];
    const timeSeriesType: TimeSeriesType = timeSeriesAndType[1];

    let newLine = this.buildLine(timeSeriesType, timeSeries);

    if (timeSeriesType === TimeSeriesType.REQUEST) {
      this.chartOptions = ChartOptions.buildDefaultChartOptions(timeSeries.dateColumnName, timeSeries.valueColumnName);
    } else {
      newLine = LineHelper.connectLines(this.chartLines[0], newLine);
      this.shouldDisplay = true;
    }

    this.chartLines = [...this.chartLines, newLine];
  }

  private buildLine(timeSeriesType: TimeSeriesType, timeSeries: TimeSeries): ChartLine {
    const lineName: string = timeSeriesType === TimeSeriesType.REQUEST ? 'Data' : 'Result';
    const points: ChartCoordinate[] = timeSeries.rows.map(row => LineHelper.buildCoordinate(new Date(row.date), row.value));
    return LineHelper.buildLine(lineName, points);
  }
}
