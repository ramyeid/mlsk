import { Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';

import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { ValuesPerColumn, CsvReaderService } from 'src/app/shared/csv/csv-reader.service';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesRequestBuilderService {

  private readonly csvReaderService: CsvReaderService;

  constructor(csvReaderService: CsvReaderService) {
    this.csvReaderService = csvReaderService;
  }

  public buildTimeSeriesAnalysisRequest(file: File, dateColumnName: string,
                                        valueColumnName: string, dateFormat: string,
                                        numberOfValues: number): Observable<TimeSeriesAnalysisRequest> {
    return this.csvReaderService.readCsv(file, [dateColumnName, valueColumnName])
      .pipe(
        switchMap((valuesPerColumn: ValuesPerColumn) => {
          return new Observable<TimeSeriesAnalysisRequest>(subscriber => {
            const timeSeriesRows: TimeSeriesRow[] = this.toTimeSeriesRow(valuesPerColumn, dateColumnName, valueColumnName);
            const timeSeries = new TimeSeries(timeSeriesRows, dateColumnName, valueColumnName, dateFormat);
            const timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(numberOfValues, timeSeries);

            subscriber.next(timeSeriesAnalysisRequest);

            subscriber.complete();
          });
        })
      );
  }

  private toTimeSeriesRow(valuesPerColumn: ValuesPerColumn, dateColumnName: string, valueColumnName: string): TimeSeriesRow[] {
    const timeSeriesRows: TimeSeriesRow[] = [];

    for (let i = 0; i < valuesPerColumn[dateColumnName].length; ++i) {
      const date: string = valuesPerColumn[dateColumnName][i];
      const value: number = +valuesPerColumn[valueColumnName][i];
      timeSeriesRows.push(new TimeSeriesRow(date, value));
    }

    return timeSeriesRows;
  }
}
