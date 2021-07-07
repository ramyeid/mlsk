import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { ValuePerColumn, ValuePerColumnPerLine, CsvReaderService } from '../../shared/csv-reader.service';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesRequestBuilderService {

  private readonly csvReaderService: CsvReaderService;

  constructor(csvReaderService: CsvReaderService) {
    this.csvReaderService = csvReaderService;
  }

  buildTimeSeriesAnalysisRequest(file: File, dateColumnName: string,
                                 valueColumnName: string, dateFormat: string,
                                 numberOfValues: number): Observable<TimeSeriesAnalysisRequest> {

    return new Observable((observable) => {
      return this.csvReaderService.readCsv(file, [dateColumnName, valueColumnName])
        .subscribe({
          next: (nextValue: ValuePerColumnPerLine) => {
            const timeSeriesRows: TimeSeriesRow[] = nextValue.map((valuePerLine: ValuePerColumn) => {
              const date: string = valuePerLine[dateColumnName];
              const value: number = +valuePerLine[valueColumnName];
              return new TimeSeriesRow(date, value);
            });
            const timeSeries = new TimeSeries(timeSeriesRows, dateColumnName, valueColumnName, dateFormat);
            const timeSeriesAnalysisRequest = new TimeSeriesAnalysisRequest(numberOfValues, timeSeries);
            observable.next(timeSeriesAnalysisRequest);
          },
          error: err => observable.error(err)
        });
    });
  }
}
