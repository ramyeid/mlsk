import { ObservableAssertionHelper } from '../test-helper/observable-assertion-helper';
import { CsvReaderService } from './csv-reader.service';

describe('CsvReaderService', () => {

  describe('Csv Validation', () => {

    it('should complete successfully if valid csv and input', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers,Sex\n12,13,14\n14,15,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.throwExceptionIfInvalidCsv(file, ['Date', 'Passengers']);

      ObservableAssertionHelper.assertOnNoEmittedItems(actualResult$, done);
    });

    it('should raise error if invalid csv and input', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers,Sex\n12,13,14\n14,15,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.throwExceptionIfInvalidCsv(file, ['Dates', 'Passenger']);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, `Column with title 'Dates' is not available in the csv file`, done);
    });

  });


  describe('readCsv', () => {

    it('should throw exception if column one does not match the file', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['a', 'b']);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, `Column with title 'a' is not available in the csv file`, done);
    });

    it('should throw exception if column two does not match the file', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['Date', 'b']);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, `Column with title 'b' is not available in the csv file`, done);
    });

    it('should throw exception if invalid content', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date;Passengers\n12;13\n14;15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, `Column with title 'Date' is not available in the csv file`, done);
    });

    it('should throw exception if invalid value lines', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14 15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

      ObservableAssertionHelper.assertOnEmittedError(actualResult$, `Unable to parse line: '14 15'`, done);
    });

    it('should correctly parse file', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

      const expectedValue = {
        Date: [ '12', '14' ],
        Passengers: [ '13', '15' ]
      };
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
    });

    it('should correctly parse file with multiple columns', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'col0,col1,col2,col3,col4\n1,2,3,4,5\n6,7,8,9,10\n11,12,13,14,15'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['col2', 'col1', 'col4']);

      const expectedValue = {
        col1: [ '2', '7', '12' ],
        col2: [ '3', '8', '13' ],
        col4: [ '5', '10', '15' ]
      };
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
    });

    it('should correctly parse file with empty values', (done: DoneFn) => {
      const service = new CsvReaderService();
      const file = new File(['\ufeff' + 'col0,col1,col2,col3,col4\n1,2,3,4,5\n6,7,,9,\n11,12,,14,'], 'test.csv', { type: 'text/html' });

      const actualResult$ = service.readCsv(file, ['col2', 'col1', 'col4']);

      const expectedValue = {
        col1: [ '2', '7', '12' ],
        col2: [ '3', ],
        col4: [ '5', ]
      };
      ObservableAssertionHelper.assertOnEmittedItems(actualResult$, [ expectedValue ], done);
    });

  });

});
