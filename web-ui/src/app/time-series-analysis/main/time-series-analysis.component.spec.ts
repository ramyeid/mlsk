import { DebugElement, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { TimeSeriesAnalysisOutputComponent } from '../output/time-series-analysis-output.component';
import { TimeSeriesAnalysisComponent } from './time-series-analysis.component';

describe('TimeSeriesAnalysisComponent', () => {

  let fixture: ComponentFixture<TimeSeriesAnalysisComponent>;
  let inputComponent: DebugElement;
  let mockOutputComponent: jasmine.SpyObj<TimeSeriesAnalysisOutputComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeSeriesAnalysisComponent ],
      schemas: [ NO_ERRORS_SCHEMA ]
    });
    fixture = TestBed.createComponent(TimeSeriesAnalysisComponent);
    inputComponent = fixture.debugElement.query(By.css('mlsk-time-series-analysis-input'));
    mockOutputComponent = jasmine.createSpyObj<TimeSeriesAnalysisOutputComponent>(['onNewRequest', 'onResult']);
    fixture.componentInstance.outputComponent = mockOutputComponent;
  });


  describe('Component Rendering', () => {

    it('should link to time series analysis input and output', () => {
      const debugElement = fixture.debugElement;

      const timeSeriesAnalysisInputSelector = debugElement.query(By.css('mlsk-time-series-analysis-input'));
      const timeSeriesAnalysisOutputSelector = debugElement.query(By.css('mlsk-time-series-analysis-output'));

      expect(timeSeriesAnalysisInputSelector).not.toBeNull();
      expect(timeSeriesAnalysisOutputSelector).not.toBeNull();
    });

  });

  describe('Input Emitter', () => {

    it('should call output on time series result', () => {
      const type: InputEmitType = InputEmitType.REQUEST;
      const row1: TimeSeriesRow = new TimeSeriesRow('1', 1);
      const row2: TimeSeriesRow = new TimeSeriesRow('2', 2);
      const timeSeries: TimeSeries = new TimeSeries([row1, row2], 'date', 'value', 'yyyyMM');

      inputComponent.triggerEventHandler('resultEmitter', [timeSeries, type]);

      expect(mockOutputComponent.onResult).toHaveBeenCalledWith([timeSeries, type]);
    });

    it('should call output on accuracy result', () => {
      const type: InputEmitType = InputEmitType.REQUEST;
      const accuracy = 74.123;

      inputComponent.triggerEventHandler('resultEmitter', [accuracy, type]);

      expect(mockOutputComponent.onResult).toHaveBeenCalledWith([accuracy, type]);
    });

    it('should call output on new request', () => {

      inputComponent.triggerEventHandler('newRequestEmitter', undefined);

      expect(mockOutputComponent.onNewRequest).toHaveBeenCalled();
    });

  });
});
