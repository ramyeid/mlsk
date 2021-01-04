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

    it('should handle error from http post forecast', () => {
      var errorEvent = new ErrorEvent('Type', {
        error : new Error('Error from backend'),
        message : 'Error from backend Message'
      });
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecast(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        error: (err) => {
          expect(err.message).toBe('An error occurred: Error from backend Message');
        }
      })
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast');
      req.error(errorEvent);
      expect(req.request.method).toBe("POST");
      httpMock.verify();
    });

    it('should handle error of type ErrorEvent from http post forecast', () => {
      var errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const timeSeriesAnalysisRequest = Helper.buildTimeSeriesAnalysisRequest();

      const actualResult$ = service.forecast(timeSeriesAnalysisRequest);

      actualResult$.subscribe({
        error: (err) => {
          expect(err.message).toBe('An error occurred: Error Event type Message');
        }
      })
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast');
      req.error(errorEvent);
      expect(req.request.method).toBe("POST");
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
      })
      const req = httpMock.expectOne('http://localhost:6766/time-series-analysis/forecast');
      req.flush(timeSeriesResult);
      expect(req.request.method).toBe("POST");
      httpMock.verify();
    });

  });

});


export class Helper {

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