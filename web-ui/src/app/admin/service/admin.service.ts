import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { EnginesDetail } from '../model/engines-detail';
import { ServerCaller } from 'src/app/shared/service/server-caller/server-caller.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService extends ServerCaller {

  constructor(httpClient: HttpClient) {
    super(httpClient, 'admin');
  }

  ping(id?: number): Observable<EnginesDetail> {
    const httpParams = new HttpParams();
    if (id) {
      httpParams.append('engineId', id!);
    }
    return this.getAndCatchError('ping', httpParams);
  }
}
