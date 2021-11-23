import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DecisionTreeService } from './decision-tree.service';
import { ClassifierStartRequest } from '../../model/classifier-start-request';
import { ClassifierStartResponse } from '../../model/classifier-start-response';
import { ClassifierDataRequest } from '../../model/classifier-data-request';
import { ClassifierRequest } from '../../model/classifier-request';
import { ClassifierDataResponse } from '../../model/classifier-data-response';

describe('DecisionTreeService', () => {

  let service: DecisionTreeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ DecisionTreeService ]
    });

    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(DecisionTreeService);
  });


  describe('Start', () => {

    it('should handle error of type error event from http post start', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const classifierStartRequest = Helper.buildClassifierStartRequest();

      const actualResult$ = service.start(classifierStartRequest);

      actualResult$.subscribe({
        next: () => expect(true).toBeFalse(),
        error: (err) => expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message')
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/start');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post start', (done: DoneFn) => {
      const classifierStartRequest = Helper.buildClassifierStartRequest();

      const actualResult$ = service.start(classifierStartRequest);

      actualResult$.subscribe({
        next: (actualValue: ClassifierStartResponse) => expect(actualValue).toEqual(Helper.buildClassifierStartResponse()),
        error: () => expect(true).toBeFalse(),
        complete: () => done()
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/start');
      req.flush(Helper.buildClassifierStartResponse());
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });


  describe('Data', () => {

    it('should handle error of type error event from http post data', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const classifierDataRequest = Helper.buildClassifierDataRequest();

      const actualResult$ = service.data(classifierDataRequest);

      actualResult$.subscribe({
        next: () => expect(true).toBeFalse(),
        error: (err) => expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message')
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/data');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post data', (done: DoneFn) => {
      const classifierDataRequest = Helper.buildClassifierDataRequest();

      const actualResult$ = service.data(classifierDataRequest);

      actualResult$.subscribe({
        next: (value) => expect(value).toBeNull(),
        error: () => expect(true).toBeFalse(),
        complete: () => done()
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/data');
      req.flush(null);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });


  describe('Predict', () => {

    it('should handle error of type error event from http post predict', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const classifieRequest = Helper.buildClassifierRequest();

      const actualResult$ = service.predict(classifieRequest);

      actualResult$.subscribe({
        next: () => expect(true).toBeFalse(),
        error: (err) => expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message')
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/predict');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post predict', (done: DoneFn) => {
      const ClassifierRequest = Helper.buildClassifierRequest();

      const actualResult$ = service.predict(ClassifierRequest);

      actualResult$.subscribe({
        next: (actualValue: ClassifierDataResponse) => expect(actualValue).toEqual(Helper.buildClassifierDataResponse()),
        error: () => expect(true).toBeFalse(),
        complete: () => done()
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/predict');
      req.flush(Helper.buildClassifierDataResponse());
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });

  describe('Compute Predict Accuracy', () => {

    it('should handle error of type error event from http post compute predict accuracy', () => {
      const errorEvent = new ErrorEvent('Type', {
        error : new ErrorEvent('Error Event type'),
        message : 'Error Event type Message'
      });
      const classifierRequest = Helper.buildClassifierRequest();

      const actualResult$ = service.computePredictAccuracy(classifierRequest);

      actualResult$.subscribe({
        next: () => expect(true).toBeFalse(),
        error: (err) => expect(err.message).toBe('Error while calling Service; code 0: Error Event type Message')
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/predict-accuracy');
      req.error(errorEvent);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

    it('should return result from http post compute predict accuracy', (done: DoneFn) => {
      const classifierRequest = Helper.buildClassifierRequest();

      const actualResult$ = service.computePredictAccuracy(classifierRequest);

      actualResult$.subscribe({
        next: (actualValue: number) => expect(actualValue).toEqual(123.1),
        error: () => expect(true).toBeFalse(),
        complete: () => done()
      });
      const req = httpMock.expectOne('http://localhost:8080/decision-tree/predict-accuracy');
      req.flush(123.1);
      expect(req.request.method).toBe('POST');
      httpMock.verify();
    });

  });

});


class Helper {

  private constructor() { }

  static buildClassifierStartRequest(): ClassifierStartRequest {
    return new ClassifierStartRequest('pred', [ 'col0', 'col1' ], 10);
  }

  static buildClassifierStartResponse(): ClassifierStartResponse {
    return new ClassifierStartResponse('requestId');
  }

  static buildClassifierDataRequest(): ClassifierDataRequest {
    return new ClassifierDataRequest('col', [ 1, 0, 1 ], 'requestId');
  }

  static buildClassifierRequest(): ClassifierRequest {
    return new ClassifierRequest('requestId');
  }

  static buildClassifierDataResponse(): ClassifierDataResponse {
    return new ClassifierDataResponse('col', [ 0, 1, 1 ]);
  }
}
