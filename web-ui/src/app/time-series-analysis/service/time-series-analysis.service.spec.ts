import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TimeSeriesAnalysisService } from './time-series-analysis.service';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';

describe('TimeSeriesAnalysisService', () => {

  let service: TimeSeriesAnalysisService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ TimeSeriesAnalysisService ]
    });

    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(TimeSeriesAnalysisService);
  });


  describe('Forecast', () => {

    it('should handle error of type error event from http post forecast', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecast(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        error: (err) => {
          expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message');
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post forecast', (done: DoneFn) => {
      const timeSeriesResult = Helper.buildTimeSeriesResult();
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecast(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        next: (actualValue: TimeSeries) => {
          expect(actualValue).toEqual(Helper.buildTimeSeriesResult());
          done();
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast');
      req.flush(timeSeriesResult);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });


  describe('Predict', () => {

    it('should handle error of type error event from http post predict', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.predict(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        error: (err) => {
          expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message');
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/predict');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post predict', (done: DoneFn) => {
      const timeSeriesResult = Helper.buildTimeSeriesResult();
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.predict(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        next: (actualValue: TimeSeries) => {
          expect(actualValue).toEqual(Helper.buildTimeSeriesResult());
          done();
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/predict');
      req.flush(timeSeriesResult);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });


  describe('Forecast vs Actual', () => {

    it('should handle error of type error event from http post forecast vs actual', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecastVsActual(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        error: (err) => {
          expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message');
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast-vs-actual');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post forecast vs actual', (done: DoneFn) => {
      const timeSeriesResult = Helper.buildTimeSeriesResult();
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecastVsActual(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        next: (actualValue: TimeSeries) => {
          expect(actualValue).toEqual(Helper.buildTimeSeriesResult());
          done();
        }
      });
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast-vs-actual');
      req.flush(timeSeriesResult);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });

});


class Helper {

  private constructor() { }

  static buildTimeSeriesResult(): TimeSeries {
    const timeSeriesRow1 = new TimeSeriesRow('4', 44);
    const timeSeriesRow2 = new TimeSeriesRow('5', 55);
    const rows = [timeSeriesRow1, timeSeriesRow2];
    return new TimeSeries(rows, 'Date', 'Pass', 'yyyyMM');
  }

  static buildTimeSeriesAnalysisRequest(): TimeSeriesAnalysisRequest {
    const timeSeries = Helper.buildTimeSeriesRequest();
    return new TimeSeriesAnalysisRequest(3, timeSeries);
  }

  private static buildTimeSeriesRequest(): TimeSeries {
    const timeSeriesRow1 = new TimeSeriesRow('1', 11);
    const timeSeriesRow2 = new TimeSeriesRow('2', 22);
    const timeSeriesRow3 = new TimeSeriesRow('3', 33);
    const rows = [timeSeriesRow1, timeSeriesRow2, timeSeriesRow3];
    return new TimeSeries(rows, 'Date', 'Pass', 'yyyyMM');
  }
}
