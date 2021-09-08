import { ConfigurationService } from './configuration.service';
import {Constants } from '../utils/constants';
import { environment } from 'src/environments/environment';

describe('ConfigurationService', () => {

  let service: ConfigurationService;
  let serverHostBackup: string;
  let serverPortBackup: string;

  beforeEach(() => {
    service = new ConfigurationService();
    serverHostBackup = environment.serverHost;
    serverPortBackup = environment.serverPort;
  });

  afterEach(() => {
    environment.serverHost = serverHostBackup;
    environment.serverPort = serverPortBackup;
  });

  describe('Get', () => {

    it('should get server host from environment variables', () => {

      const actualValue = service.getServerHost();

      expect(actualValue).toBe('localhost');
    });

    it('should get server port from environment variables', () => {

      const actualValue = service.getServerPort();

      expect(actualValue).toBe(8080);
    });

  });

  describe('Save', () => {

    it('should override variables on save', () => {
      const formValues: { [id: string]: string | number } = {
        [ Constants.SERVER_HOST_FORM ]: 'myNewLocalHost',
        [ Constants.SERVER_PORT_FORM ]: '109201'
      };

      service.saveConfiguration(formValues);

      expect(service.getServerHost()).toBe('myNewLocalHost');
      expect(service.getServerPort()).toBe(109201);
    });

  });

});
