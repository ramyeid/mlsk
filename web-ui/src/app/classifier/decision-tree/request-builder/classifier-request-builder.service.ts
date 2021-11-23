import { Injectable } from '@angular/core';
import { Observable, of, switchMap } from 'rxjs';

import { ClassifierStartRequest } from '../model/classifier-start-request';
import { ClassifierDataRequest } from '../model/classifier-data-request';
import { ClassifierRequest } from '../model/classifier-request';
import { CsvReaderService, ValuesPerColumn } from 'src/app/shared/csv/csv-reader.service';

@Injectable({
  providedIn: 'root'
})
export class ClassifierRequestBuilderService {

  private readonly csvReaderService: CsvReaderService;

  constructor(csvReaderService: CsvReaderService) {
    this.csvReaderService = csvReaderService;
  }

  public buildClassifierStartRequest(predictionColumnName: string, actionColumnNames: string[], numberOfValues: number): Observable<ClassifierStartRequest> {
    return of(new ClassifierStartRequest(predictionColumnName, actionColumnNames, numberOfValues));
  }

  public buildClassifierDataRequests(file: File, predictionColumnName: string, actionColumnNames: string[], requestId: string): Observable<ClassifierDataRequest> {
    return this.csvReaderService.readCsv(file, [predictionColumnName, ...actionColumnNames])
      .pipe(
        switchMap((valuesPerColumn: ValuesPerColumn) => {
          return new Observable<ClassifierDataRequest>(subscriber => {
            const allColumns: string[] = [ predictionColumnName, ...actionColumnNames ];

            allColumns.forEach(columnName => {
              subscriber.next(this.toClassifierDataRequest(valuesPerColumn, columnName, requestId));
            });

            subscriber.complete();
          });
        })
      );
  }

  public buildClassifierRequest(requestId: string): Observable<ClassifierRequest> {
    return of(new ClassifierRequest(requestId));
  }

  private toClassifierDataRequest(valuesPerColumn: ValuesPerColumn, columnName: string, requestId: string): ClassifierDataRequest {
    const values: number[] = valuesPerColumn[columnName].map(value => +value);

    return new ClassifierDataRequest(columnName, values, requestId);
  }
}
