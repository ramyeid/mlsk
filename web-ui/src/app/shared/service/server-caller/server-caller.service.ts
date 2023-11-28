import { HttpClient, HttpErrorResponse, HttpParams, HttpRequest } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';

import { environment } from 'src/environments/environment';

export abstract class ServerCaller {

  private readonly httpClient: HttpClient;
  private readonly baseResource;

  constructor(httpClient: HttpClient, baseResource: string) {
    this.httpClient = httpClient;
    this.baseResource = baseResource;
  }

  // Implemented in this manner to make sure piped calls to service are launched sequentially.
  protected postAndCatchError<Request, Response>(resource: string, body: Request): Observable<Response> {
    const serverResource: string = this.buildResource();
    return new Observable(subscriber => {
      this.httpClient.post<Response>(`${serverResource}/${resource}`, body)
        .pipe(
          catchError(this.handleError)
        ).subscribe({
          next: response => subscriber.next(response),
          error: err => subscriber.error(err),
          complete: () => subscriber.complete()
        });
    });
  }

  protected getAndCatchError<Response>(resource: string, params: HttpParams): Observable<Response> {
    const serverResource: string = this.buildResource();
    return new Observable(subscriber => {
      this.httpClient.get<Response>(`${serverResource}/${resource}`, {params: params})
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

  private buildResource(): string {
    return `http://${environment.serverHost}:${environment.serverPort}/${this.baseResource}`;
  }
}
