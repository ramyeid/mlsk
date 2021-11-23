import { Component, ViewChild } from '@angular/core';

import { TimeSeries } from '../model/time-series';
import { TimeSeriesEmittedType } from '../model/time-series-emitted-type';
import { TimeSeriesAnalysisOutputComponent } from '../output/time-series-analysis-output.component';

@Component({
  selector: 'mlsk-time-series-analysis',
  templateUrl: './time-series-analysis.component.html'
})
export class TimeSeriesAnalysisComponent {

  @ViewChild(TimeSeriesAnalysisOutputComponent) outputComponent: TimeSeriesAnalysisOutputComponent;

  onResult(resultAndType: [TimeSeries | number, TimeSeriesEmittedType]): void {
    this.outputComponent.onResult(resultAndType);
  }

  onNewRequest(): void {
    this.outputComponent.onNewRequest();
  }

}
