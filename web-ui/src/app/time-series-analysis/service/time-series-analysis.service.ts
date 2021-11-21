import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from 'src/environments/environment';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesAnalysisService {

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

  computeForecastAccuracy(body: TimeSeriesAnalysisRequest): Observable<number> {
    return this.postAndCatchError('forecast-accuracy', body);
  }

  private postAndCatchError<T>(resource: string, body: TimeSeriesAnalysisRequest): Observable<T> {
    const baseUrl: string = this.buildBaseUrl();
    return new Observable(subscriber => {
      this.httpClient.post<T>(`${baseUrl}/${resource}`, body)
        .pipe(
          catchError(this.handleError)
        ).subscribe({
          next: response => subscriber.next(response),
          error: err => subscriber.error(err),
          complete: () => subscriber.complete()
        });
    });
  }

  private handleError(err: HttpErrorResponse): Observable<never> {
    let message = 'Unable to call Service';
    if (err.error.message) {
      message = `Error while calling Service; code ${err.status}: ${err.error.message}`;
    }
    return throwError(() => new Error(message));
  }

  private buildBaseUrl(): string {
    return `http://${environment.serverHost}:${environment.serverPort}/time-series-analysis`;
  }
}
