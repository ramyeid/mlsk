import { Injectable } from '@angular/core';

import { environment } from 'src/environments/environment';
import { Constants } from '../utils/constants';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  public getServerHost(): string {
    return environment.serverHost;
  }

  public getServerPort(): number {
    return Number(environment.serverPort);
  }

  public saveConfiguration(formValues: { [id: string]: string | number }): void {
    this.updateEnvironmentVariable(formValues);
  }

  private updateEnvironmentVariable(formValues: { [id: string]: string | number }): void {
    environment.serverHost = String(formValues[Constants.SERVER_HOST_FORM]);
    environment.serverPort = String(formValues[Constants.SERVER_PORT_FORM]);
  }
}
