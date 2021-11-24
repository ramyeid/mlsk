import { Observable, of } from 'rxjs';

import { ObservableAssertionHelper } from 'src/app/shared/test-helper/observable-assertion-helper';
import { ClassifierRequestBuilderService } from './classifier-request-builder.service';
import { CsvReaderService, ValuesPerColumn } from 'src/app/shared/csv/csv-reader.service';
import { ClassifierStartRequest } from '../model/classifier-start-request';
import { ClassifierDataRequest } from '../model/classifier-data-request';
import { ClassifierRequest } from '../model/classifier-request';

describe('ClassifierRequestBuilderService', () => {

  let service: ClassifierRequestBuilderService;
  let mockCsvReader: jasmine.SpyObj<CsvReaderService>;
  const file: File = new File([], 'test.txt');

  beforeEach(() => {
    mockCsvReader = jasmine.createSpyObj<CsvReaderService>(['readCsv']);
    service = new ClassifierRequestBuilderService(mockCsvReader);
  });

  describe('ClassifierStartRequest', () => {

    it('should build observable with classifier start request', (done: DoneFn) => {
      const predictionColumnName = 'predictionColumn';
      const actionColumnNames = [ 'col0', 'col1' ];
      const numberOfValues = 12;

      const actualResult$ = service.buildClassifierStartRequest(predictionColumnName, actionColumnNames, numberOfValues);

      const expectedValue: ClassifierStartRequest = new ClassifierStartRequest(predictionColumnName, actionColumnNames, numberOfValues);
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
    });

  });


  describe('ClassifierDataRequest', () => {

    it('should forward error from csv reader service', (done: DoneFn) => {
      const predictionColumnName = 'predictionColumn';
      const actionColumnNames = [ 'col0', 'col1' ];
      const requestId = 'requestId';
      const readCsvResult$ = new Observable<ValuesPerColumn>(subscriber => {
        subscriber.error('Error from ReadCsvService');
        subscriber.complete();
      });
      mockCsvReader.readCsv.and.returnValue(readCsvResult$);

      const actualResult$ = service.buildClassifierDataRequests(file, predictionColumnName, actionColumnNames, requestId);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, 'Error from ReadCsvService', done);
    });

    it('should map result from csv reader service to classifier data requests', (done: DoneFn) => {
      const predictionColumnName = 'predictionColumn';
      const actionColumnNames = [ 'col0', 'col1' ];
      const requestId = 'requestId';
      const readCsvResult$ = of({
        predictionColumn: [ '1', '2', '3' ],
        col0: [ '4', '5', '6' ],
        col1: [ '7', '8', '9' ]
      });
      mockCsvReader.readCsv.and.returnValue(readCsvResult$);

      const actualResult$ = service.buildClassifierDataRequests(file, predictionColumnName, actionColumnNames, requestId);

      const expectedValue1 = new ClassifierDataRequest('predictionColumn', [ 1, 2, 3 ], requestId);
      const expectedValue2 = new ClassifierDataRequest('col0', [ 4, 5, 6 ], requestId);
      const expectedValue3 = new ClassifierDataRequest('col1', [ 7, 8, 9 ], requestId);
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue1, expectedValue2, expectedValue3 ], done);
    });

  });


  describe('ClassifierRequest', () => {

    it('should build observable with classifier request', (done: DoneFn) => {
      const requestId = 'requestId';

      const actualResult$ = service.buildClassifierRequest(requestId);

      const expectedValue = new ClassifierRequest(requestId);
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
    });

  });

});
