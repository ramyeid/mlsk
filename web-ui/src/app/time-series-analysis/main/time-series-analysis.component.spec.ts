import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeSeriesAnalysisComponent } from './time-series-analysis.component';

describe('TimeSeriesAnalysisComponent', () => {

  let fixture: ComponentFixture<TimeSeriesAnalysisComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeSeriesAnalysisComponent ]
    });
    fixture = TestBed.createComponent(TimeSeriesAnalysisComponent);
  });

  it('should link to time series analysis input and output', () => {
    const nativeElement = fixture.debugElement.nativeElement;

    const timeSeriesAnalysisInputSelector = nativeElement.querySelector('app-time-series-analysis-input');
    const timeSeriesAnalysisOutputSelector = nativeElement.querySelector('app-time-series-analysis-output');

    expect(timeSeriesAnalysisInputSelector).not.toBeNull()
    expect(timeSeriesAnalysisOutputSelector).not.toBeNull()
  });
});
