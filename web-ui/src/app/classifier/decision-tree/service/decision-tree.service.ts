import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from 'src/environments/environment';
import { ClassifierStartRequest } from '../model/classifier-start-request';
import { ClassifierStartResponse } from '../model/classifier-start-response';
import { ClassifierDataRequest } from '../model/classifier-data-request';
import { ClassifierRequest } from '../model/classifier-request';
import { ClassifierDataResponse } from '../model/classifier-data-response';

@Injectable({
  providedIn: 'root'
})
export class DecisionTreeService {

  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  start(body: ClassifierStartRequest): Observable<ClassifierStartResponse> {
    return this.postAndCatchError('start', body);
  }

  data(body: ClassifierDataRequest): Observable<undefined> {
    return this.postAndCatchError('data', body);
  }

  predict(body: ClassifierRequest): Observable<ClassifierDataResponse> {
    return this.postAndCatchError('predict', body);
  }

  computePredictAccuracy(body: ClassifierRequest): Observable<number> {
    return this.postAndCatchError('predict-accuracy', body);
  }

  // Implemented in this manner to make sure piped calls to service are launched sequentially.
  private postAndCatchError<T>(resource: string, body: ClassifierStartRequest | ClassifierDataRequest | ClassifierRequest): Observable<T> {
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
    return `http://${environment.serverHost}:${environment.serverPort}/decision-tree`;
  }
}
