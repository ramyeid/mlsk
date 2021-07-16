import { Observable, of } from 'rxjs';

import { TimeSeriesRequestBuilderService } from './time-series-request-builder.service';
import { CsvReaderService, ValuePerColumnPerLine } from 'src/app/shared/csv-reader.service';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';

describe('TimeSeriesRequestBuilderService', () => {

  let service: TimeSeriesRequestBuilderService;
  let mockCsvReader: jasmine.SpyObj<CsvReaderService>;
  const file: File = new File([], 'test.txt');

  beforeEach(() => {
    mockCsvReader = jasmine.createSpyObj<CsvReaderService>(['readCsv']);
    service = new TimeSeriesRequestBuilderService(mockCsvReader);
  });

  it('should forward error from csv reader service', (done: DoneFn) => {
    const readCsvResult$ = new Observable<ValuePerColumnPerLine>(observer => {
      observer.error('Error from ReadCsvService');
    });
    mockCsvReader.readCsv.and.returnValue(readCsvResult$);

    const actualResult$ = service.buildTimeSeriesAnalysisRequest(file, 'Date', 'Passengers', 'yyyMM', 5);

    actualResult$.subscribe({
      error: (error) => {
        expect(error).toBe('Error from ReadCsvService');
        done();
      }
    });
  });

  it('should map result from csv reader service to time series analysis request', (done: DoneFn) => {
    const readCsvResult$ = of([
      { Date: '1.2', Passengers: '120' },
      { Date: '1.3', Passengers: '130' },
      { Date: '1.4', Passengers: '140'}]);
    mockCsvReader.readCsv.and.returnValue(readCsvResult$);

    const actualResult$ = service.buildTimeSeriesAnalysisRequest(file, 'Date', 'Passengers', 'yyyMM', 3);

    const expectedTimeSeriesRow1 = new TimeSeriesRow('1.2', 120);
    const expectedTimeSeriesRow2 = new TimeSeriesRow('1.3', 130);
    const expectedTimeSeriesRow3 = new TimeSeriesRow('1.4', 140);
    const expectedRows = [ expectedTimeSeriesRow1, expectedTimeSeriesRow2, expectedTimeSeriesRow3 ];
    const expectedTimeSeries = new TimeSeries(expectedRows, 'Date', 'Passengers', 'yyyMM');
    const expectedValue = new TimeSeriesAnalysisRequest(3, expectedTimeSeries);
    actualResult$.subscribe({
      next: (actualValue) => {
        expect(actualValue).toEqual(expectedValue);
        done();
      }
    });
  });

});
