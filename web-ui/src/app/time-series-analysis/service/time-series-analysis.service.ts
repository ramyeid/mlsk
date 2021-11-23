import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { ServerCaller } from 'src/app/shared/service/server-caller/server-caller.service';

@Injectable({
  providedIn: 'root'
})
export class TimeSeriesAnalysisService extends ServerCaller {

  constructor(httpClient: HttpClient) {
    super(httpClient, 'time-series-analysis');
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
}
