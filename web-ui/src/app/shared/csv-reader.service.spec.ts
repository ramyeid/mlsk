import { CsvReaderService, ValuePerColumnPerLine } from './csv-reader.service';

describe('CsvReaderService', () => {

  it('should throw exception if column one does not match the file', (done: DoneFn) => {
    const service = new CsvReaderService();
    const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

    const actualResult$ = service.readCsv(file, ['a', 'b']);

    actualResult$.subscribe({
      error: (error) => {
        expect(error.message).toBe(`Column with title 'a' is not available in the csv file`);
        done();
      }
    });
  });

  it('should throw exception if column two does not match the file', (done: DoneFn) => {
    const service = new CsvReaderService();
    const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

    const actualResult$ = service.readCsv(file, ['Date', 'b']);

    actualResult$.subscribe({
      error: (error) => {
        expect(error.message).toBe(`Column with title 'b' is not available in the csv file`);
        done();
      }
    });
  });

  it('should throw exception if invalid content', (done: DoneFn) => {
    const service = new CsvReaderService();
    const file = new File(['\ufeff' + 'Date;Passengers\n12;13\n14;15'], 'test.csv', { type: 'text/html' });

    const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

    actualResult$.subscribe({
      error: (error) => {
        expect(error.message).toBe(`Column with title 'Date' is not available in the csv file`);
        done();
      }
    });
  });

  it('should throw exception if invalid value lines', (done: DoneFn) => {
    const service = new CsvReaderService();
    const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14 15'], 'test.csv', { type: 'text/html' });

    const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

    actualResult$.subscribe({
      error: (error) => {
        expect(error.message).toBe(`Unable to parse line: '14 15'`);
        done();
      }
    });
  });

  it('should correctly parse file', (done: DoneFn) => {
    const service = new CsvReaderService();
    const file = new File(['\ufeff' + 'Date,Passengers\n12,13\n14,15'], 'test.csv', { type: 'text/html' });

    const actualResult$ = service.readCsv(file, ['Date', 'Passengers']);

    actualResult$.subscribe({
      next: (value: ValuePerColumnPerLine) => {
        const expected = [
          { Date: '12', Passengers: '13' },
          { Date: '14', Passengers: '15' }
        ];
        expect(value).toEqual(expected);
        done();
      }
    });
  });

});
