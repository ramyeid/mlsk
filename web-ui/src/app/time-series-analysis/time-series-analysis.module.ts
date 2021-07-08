import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from './../shared/shared.module';
import { TimeSeriesAnalysisComponent } from './main/time-series-analysis.component';
import { TimeSeriesAnalysisInputComponent } from './input/time-series-analysis-input.component';
import { TimeSeriesAnalysisOutputComponent } from './output/time-series-analysis-output.component';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      { path: '', component: TimeSeriesAnalysisComponent }
    ]),
  ],
  declarations: [
    TimeSeriesAnalysisComponent,
    TimeSeriesAnalysisInputComponent,
    TimeSeriesAnalysisOutputComponent
  ]
})
export class TimeSeriesAnalysisModule { }
