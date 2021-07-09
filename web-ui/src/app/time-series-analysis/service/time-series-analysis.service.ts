import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesAnalysisService {

  private static readonly BASE_URL = 'http://localhost:6766/time-series-analysis';
  // public static final String FORECAST_VS_ACTUAL_URL = '/time-series-analysis/forecast-vs-actual';
  // public static final String FORECAST_ACCURACY_URL = '/time-series-analysis/forecast-accuracy';
  // public static final String PREDICATE_URL = '/time-series-analysis/predict';

  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  forecast(body: TimeSeriesAnalysisRequest): Observable<TimeSeries> {
    return this.httpClient.post<TimeSeries>(`${TimeSeriesAnalysisService.BASE_URL}/forecast`, body)
        .pipe(
          catchError(this.handleError)
        );
  }

  private handleError(err: any): Observable<never> {
    let errorMessage: string;
    if (err.error instanceof ErrorEvent) {
      errorMessage = `An error occurred: ${err.error.message}`;
    } else {
      errorMessage = `Backend returned code ${err.status}: ${err.error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }

}
