import { Component, ViewChild } from '@angular/core';

import { TimeSeries } from '../model/time-series';
import { TimeSeriesType } from '../model/time-series-type';
import { TimeSeriesAnalysisOutputComponent } from '../output/time-series-analysis-output.component';

@Component({
  selector: 'app-time-series-analysis',
  templateUrl: './time-series-analysis.component.html',
  styleUrls: ['./time-series-analysis.component.css']
})
export class TimeSeriesAnalysisComponent {

  @ViewChild(TimeSeriesAnalysisOutputComponent) outputComponent: TimeSeriesAnalysisOutputComponent;

  onTimeSeriesResult(timeSeriesAndType: [TimeSeries, TimeSeriesType]): void {
    this.outputComponent.onTimeSeriesResult(timeSeriesAndType);
  }

  onNewRequest(): void {
    this.outputComponent.onNewRequest();
  }

}
