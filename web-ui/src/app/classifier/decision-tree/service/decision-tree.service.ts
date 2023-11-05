import { Injectable } from '@angular/core';
import { HttpClient  } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ServerCaller } from 'src/app/shared/service/server-caller/server-caller.service';
import { ClassifierStartRequest } from '../../model/classifier-start-request';
import { ClassifierStartResponse } from '../../model/classifier-start-response';
import { ClassifierDataRequest } from '../../model/classifier-data-request';
import { ClassifierRequest } from '../../model/classifier-request';
import { ClassifierResponse } from '../../model/classifier-response';

@Injectable({
  providedIn: 'root'
})
export class DecisionTreeService extends ServerCaller {

  constructor(httpClient: HttpClient) {
    super(httpClient, 'decision-tree');
  }

  start(body: ClassifierStartRequest): Observable<ClassifierStartResponse> {
    return this.postAndCatchError('start', body);
  }

  data(body: ClassifierDataRequest): Observable<undefined> {
    return this.postAndCatchError('data', body);
  }

  predict(body: ClassifierRequest): Observable<ClassifierResponse> {
    return this.postAndCatchError('predict', body);
  }

  computePredictAccuracy(body: ClassifierRequest): Observable<number> {
    return this.postAndCatchError('predict-accuracy', body);
  }
}
