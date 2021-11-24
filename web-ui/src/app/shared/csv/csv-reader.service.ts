import { Injectable } from '@angular/core';
import { Observable, Subscriber } from 'rxjs';

export type ValuesPerColumn = { [key: string]: string[] };

@Injectable({
  providedIn: 'root'
})
export class CsvReaderService {

  private static readonly SEPERATOR: string = ',';
  private static readonly END_LINE: string = '\n';

  public throwExceptionIfInvalidCsv(file: File, columns: string[]): Observable<never> {
    return new Observable(subscriber => {
      const fileReader = new FileReader();

      fileReader.onload = (): void => {
        const contentLines = this.toFileContent(fileReader.result);

        const columnIndexes: number[] = this.toColumnIndexes(contentLines, columns);

        this.errorIfColumnsNotFound(subscriber, columnIndexes, columns);
      };

      fileReader.onerror = (error): void => subscriber.error(error);
      fileReader.onabort = (error): void => subscriber.error(error);
      fileReader.onloadend = (): void => subscriber.complete();

      return fileReader.readAsText(file);
    });
  }

  public readCsv(file: File, columns: string[]): Observable<ValuesPerColumn> {

    return new Observable(subscriber => {
      const fileReader = new FileReader();

      fileReader.onload = (): void => {
        const contentLines = this.toFileContent(fileReader.result);

        const columnIndexes: number[] = this.toColumnIndexes(contentLines, columns);

        if (this.errorIfColumnsNotFound(subscriber, columnIndexes, columns)) {
          return;
        }

        try {
          const values = this.buildValuesPerColumn(columnIndexes, columns, contentLines);
          subscriber.next(values);
        } catch (error) {
          subscriber.error(error);
        }
      };

      fileReader.onerror = (error): void => subscriber.error(error);
      fileReader.onabort = (error): void => subscriber.error(error);
      fileReader.onloadend = (): void => subscriber.complete();

      return fileReader.readAsText(file);
    });
  }

  private toFileContent(content: string | ArrayBuffer | null): string[] {
    return (content as string)?.split(CsvReaderService.END_LINE)
      .map(line => line.replace('\r', ''))
      .filter(line => line.trim() != '');
  }

  private toColumnIndexes(fileContent: string[], columnTitles: string[]): number[] {
    const titleLine: string = fileContent[0];
    return columnTitles.map(columnTitle => {
      const titles: string[] = titleLine.split(CsvReaderService.SEPERATOR);
      return titles.indexOf(columnTitle);
    });
  }

  private errorIfColumnsNotFound<T>(subscriber: Subscriber<T>, columnIndexes: number[], columns: string[]): boolean {
    if (columnIndexes.includes(-1)) {
      const errorIndex: number = columnIndexes.indexOf(-1);
      subscriber.error(new Error(`Column with title '${columns[errorIndex]}' is not available in the csv file`));
      subscriber.complete();
      return true;
    }
    return false;
  }

  private buildValuesPerColumn(columnIndexes: number[], columns: string[], contentLines: string[]): ValuesPerColumn {
    const valuesPerColumn: ValuesPerColumn = this.buildEmptyValuesPerColumn(columns);
    const numberOfColumns = contentLines[0].split(CsvReaderService.SEPERATOR).length;

    for (let i = 1; i < contentLines.length; i++) {
      this.toColumnTitleAndValues(columnIndexes, columns, contentLines[i], numberOfColumns)
        .forEach(columnTitleAndValue => valuesPerColumn[columnTitleAndValue[0]].push(columnTitleAndValue[1]));
    }
    return valuesPerColumn;
  }

  private buildEmptyValuesPerColumn(columns: string[]): ValuesPerColumn {
    const valuesPerColumn: ValuesPerColumn = {};

    columns.forEach(columnName => valuesPerColumn[columnName] = []);

    return valuesPerColumn;
  }

  private toColumnTitleAndValues(columnIndexes: number[], columns: string[], line: string, numberOfColumns: number): [string, string][] {
    const valuesInLine: string[] = line.split(CsvReaderService.SEPERATOR);

    this.throwExceptionIfColumnAndValueLengthNotEqual(numberOfColumns, valuesInLine, line);

    const columnTitleAndValues: [string, string][] = [];
    for (let i = 0; i < columns.length; ++i) {
      const columnTitle: string = columns[i];
      const value: string = valuesInLine[columnIndexes[i]];
      if (value) {
        columnTitleAndValues.push([columnTitle, value]);
      }
    }
    return columnTitleAndValues;
  }

  private throwExceptionIfColumnAndValueLengthNotEqual(numberOfColumns: number, valuesPerLine: string[], line: string): void {
    if (numberOfColumns !== valuesPerLine.length) {
      throw new Error(`Unable to parse line: '${line}'`);
    }
  }

}
