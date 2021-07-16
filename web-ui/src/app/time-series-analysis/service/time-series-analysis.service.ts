import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesAnalysisService {

  private static readonly BASE_URL = 'http://localhost:6766/time-series-analysis';

  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  forecast(body: TimeSeriesAnalysisRequest): Observable<TimeSeries> {
    return this.postAndCatchError('forecast', body);
  }

  predict(body: TimeSeriesAnalysisRequest): Observable<TimeSeries> {
    return this.postAndCatchError('predict', body);
  }

  forecastVsActual(body: TimeSeriesAnalysisRequest): Observable<TimeSeries> {
    return this.postAndCatchError('forecast-vs-actual', body);
  }

  private postAndCatchError(resource: string, body: TimeSeriesAnalysisRequest): Observable<TimeSeries> {
    return this.httpClient.post<TimeSeries>(`${TimeSeriesAnalysisService.BASE_URL}/${resource}`, body)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    let message = 'Unable to call Service';
    if (err.error.message) {
      message = `Error while calling Service; code ${err.status}: ${err.error.message}`;
    }
    return throwError(new Error(message));
  }
}
