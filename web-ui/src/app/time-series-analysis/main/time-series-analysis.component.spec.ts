import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { TimeSeriesType } from '../model/time-series-type';
import { TimeSeriesAnalysisOutputComponent } from '../output/time-series-analysis-output.component';
import { TimeSeriesAnalysisComponent } from './time-series-analysis.component';

describe('TimeSeriesAnalysisComponent', () => {

  let fixture: ComponentFixture<TimeSeriesAnalysisComponent>;
  let inputComponent: DebugElement;
  let mockOutputComponent: jasmine.SpyObj<TimeSeriesAnalysisOutputComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeSeriesAnalysisComponent ]
    });
    fixture = TestBed.createComponent(TimeSeriesAnalysisComponent);
    inputComponent = fixture.debugElement.query(By.css('app-time-series-analysis-input'));
    mockOutputComponent = jasmine.createSpyObj<TimeSeriesAnalysisOutputComponent>(['onNewRequest', 'onTimeSeriesResult']);
    fixture.componentInstance.outputComponent = mockOutputComponent;
  });


  describe('Component Rendering', () => {

    it('should link to time series analysis input and output', () => {
      const debugElement = fixture.debugElement;

      const timeSeriesAnalysisInputSelector = debugElement.query(By.css('app-time-series-analysis-input'));
      const timeSeriesAnalysisOutputSelector = debugElement.query(By.css('app-time-series-analysis-output'));

      expect(timeSeriesAnalysisInputSelector).not.toBeNull();
      expect(timeSeriesAnalysisOutputSelector).not.toBeNull();
    });

  });

  describe('Input Emitter', () => {

    it('should call output on time series result', () => {
      const type: TimeSeriesType = TimeSeriesType.REQUEST;
      const row1: TimeSeriesRow = new TimeSeriesRow('1', 1);
      const row2: TimeSeriesRow = new TimeSeriesRow('2', 2);
      const timeSeries: TimeSeries = new TimeSeries([row1, row2], 'date', 'value', 'yyyyMM');

      inputComponent.triggerEventHandler('timeSeriesResultEmitter', [timeSeries, type]);

      expect(mockOutputComponent.onTimeSeriesResult).toHaveBeenCalledWith([timeSeries, type]);
    });

    it('should call output on new request', () => {

      inputComponent.triggerEventHandler('newRequestEmitter', undefined);

      expect(mockOutputComponent.onNewRequest).toHaveBeenCalled();
    });

  });
});
