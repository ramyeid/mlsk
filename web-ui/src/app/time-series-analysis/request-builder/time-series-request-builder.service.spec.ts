import { Observable, of } from 'rxjs';

import { ObservableAssertionHelper } from 'src/app/shared/test-helper/observable-assertion-helper';
import { TimeSeriesRequestBuilderService } from './time-series-request-builder.service';
import { CsvReaderService, ValuesPerColumn } from 'src/app/shared/csv/csv-reader.service';
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
    const readCsvResult$ = new Observable<ValuesPerColumn>(subscriber => {
      subscriber.error('Error from ReadCsvService');
    });
    mockCsvReader.readCsv.and.returnValue(readCsvResult$);

    const actualResult$ = service.buildTimeSeriesAnalysisRequest(file, 'Date', 'Passengers', 'yyyMM', 5);

    ObservableAssertionHelper.assertOnEmittedError(actualResult$, 'Error from ReadCsvService', done);
  });

  it('should map result from csv reader service to time series analysis request', (done: DoneFn) => {
    const readCsvResult$ = of({
      Date: [ '1.2', '1.3', '1.4' ],
      Passengers: [ '120', '130', '140' ]
    });
    mockCsvReader.readCsv.and.returnValue(readCsvResult$);

    const actualResult$ = service.buildTimeSeriesAnalysisRequest(file, 'Date', 'Passengers', 'yyyMM', 3);

    const expectedTimeSeriesRow1 = new TimeSeriesRow('1.2', 120);
    const expectedTimeSeriesRow2 = new TimeSeriesRow('1.3', 130);
    const expectedTimeSeriesRow3 = new TimeSeriesRow('1.4', 140);
    const expectedRows = [ expectedTimeSeriesRow1, expectedTimeSeriesRow2, expectedTimeSeriesRow3 ];
    const expectedTimeSeries = new TimeSeries(expectedRows, 'Date', 'Passengers', 'yyyMM');
    const expectedValue = new TimeSeriesAnalysisRequest(3, expectedTimeSeries);
    ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
  });

});
