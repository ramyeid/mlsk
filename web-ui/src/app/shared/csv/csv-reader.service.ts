import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type ValuePerColumn = { [key: string]: string };
export type ValuePerColumnPerLine = ValuePerColumn[];

@Injectable({
  providedIn: 'root'
})
export class CsvReaderService {

  private static readonly SEPERATOR: string = ',';
  private static readonly END_LINE: string = '\n';

  readCsv(file: File, columns: string[]): Observable<ValuePerColumnPerLine> {

    return new Observable((observable) => {
      const fileReader = new FileReader();

      fileReader.onload = (): void => {
        const content = fileReader.result;
        const contentLines = (content as string)?.split(CsvReaderService.END_LINE).map(line => line.replace('\r', ''));

        const columnIndexes: number[] = this.toColumnIndexes(contentLines, columns);
        if (columnIndexes.includes(-1)) {
          const errorIndex: number = columnIndexes.indexOf(-1);
          observable.error(new Error(`Column with title '${columns[errorIndex]}' is not available in the csv file`));
          return;
        }

        try {
          const values = this.buildValuePerColumnPerLine(columnIndexes, columns, contentLines);
          observable.next(values);
        } catch (error) {
          observable.error(error);
        }
      };

      fileReader.onerror = (error): void => observable.error(error);
      fileReader.onabort = (error): void => observable.error(error);
      fileReader.onloadend = (): void => observable.complete();

      return fileReader.readAsText(file);
    });
  }

  private buildValuePerColumnPerLine(columnIndexes: number[], columns: string[], contentLines: string[]): ValuePerColumnPerLine {
    const values: ValuePerColumnPerLine = [];
    for (let i = 1; i < contentLines.length; i++) {
      const line: string = contentLines[i];
      values.push(this.buildValuePerColumn(columnIndexes, columns, line));
    }
    return values;
  }

  private buildValuePerColumn(columnIndexes: number[], columns: string[], line: string): ValuePerColumn {
    const valuesPerLine: string[] = line.split(CsvReaderService.SEPERATOR);
    const valuePerColumn: ValuePerColumn = {};

    this.throwExceptionIfColumnAndValueLengthNotEqual(columnIndexes, valuesPerLine);

    for (let j = 0; j < columnIndexes.length; j++) {
      const columnTitle: string = columns[j];
      const value: string = valuesPerLine[j];
      valuePerColumn[columnTitle] = value;
    }

    return valuePerColumn;
  }

  private toColumnIndexes(fileContent: string[], columnTitles: string[]): number[] {
    const titleLine: string = fileContent[0];
    return columnTitles.map(columnTitle => this.retrieveColumnIndex(titleLine, columnTitle));
  }

  private retrieveColumnIndex(titleLine: string, columnTitle: string): number {
    const titles: string[] = titleLine.split(CsvReaderService.SEPERATOR);
    return titles.indexOf(columnTitle);
  }

  private throwExceptionIfColumnAndValueLengthNotEqual(columnIndexes: number[], valuesPerLine: string[]): void {
    if (columnIndexes.length !== valuesPerLine.length) {
      throw new Error(`Unable to parse line: '${valuesPerLine}'`);
    }
  }

}
