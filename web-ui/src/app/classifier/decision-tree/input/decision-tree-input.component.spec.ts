import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { from, Observable, of } from 'rxjs';

import { InputListComponent } from 'src/app/shared/component/input-list/input-list.component';
import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';
import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { DecisionTreeInputComponent } from './decision-tree-input.component';
import { Constants } from '../utils/constants';
import { DecisionTreeService } from '../service/decision-tree.service';
import { ClassifierRequestBuilderService } from '../../request-builder/classifier-request-builder.service';
import { ClassifierStartRequest } from '../../model/classifier-start-request';
import { ClassifierDataRequest } from '../../model/classifier-data-request';
import { ClassifierResponse } from '../../model/classifier-response';
import { ClassifierStartResponse } from '../../model/classifier-start-response';
import { ClassifierRequest } from '../../model/classifier-request';

describe('DecisionTreeInputComponent', () => {

  let component: DecisionTreeInputComponent;
  let fixture: ComponentFixture<DecisionTreeInputComponent>;
  let mockService: jasmine.SpyObj<DecisionTreeService>;
  let mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>;
  let mockCsvReaderService: jasmine.SpyObj<CsvReaderService>;

  beforeEach(() => {
    mockService = jasmine.createSpyObj<DecisionTreeService>(['start', 'data', 'predict', 'computePredictAccuracy']);
    mockRequestBuilderService = jasmine.createSpyObj<ClassifierRequestBuilderService>(['buildClassifierStartRequest', 'buildClassifierDataRequests', 'buildClassifierRequest']);
    mockCsvReaderService = jasmine.createSpyObj<CsvReaderService>(['throwExceptionIfInvalidCsv']);

    TestBed.configureTestingModule({
      imports: [ ReactiveFormsModule, FormsModule, MatIconModule ],
      declarations: [ InputListComponent, DecisionTreeInputComponent ],
      providers: [
        { provide: DecisionTreeService, useValue: mockService },
        { provide: ClassifierRequestBuilderService, useValue: mockRequestBuilderService },
        { provide: CsvReaderService, useValue: mockCsvReaderService }
      ]
    });

    fixture = TestBed.createComponent(DecisionTreeInputComponent);
    component = fixture.componentInstance;
    component.ngAfterViewInit();

    FormHelper.setupEmittedItemsSubscriber(fixture);
    FormHelper.setupNewRequestSubscriber(fixture);
  });


  describe('Input Validation', () => {

    it('should disable button and set form as invalid if form is empty', fakeAsync(() => {

      fixture.detectChanges();
      tick(1000);

      const expectedErrorMessagePerInput = { };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if prediction column name form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.PREDICTION_COLUMN_NAME_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.PREDICTION_COLUMN_NAME_FORM ]: 'Prediction column name is required.',
        [ Constants.ACTION_COLUMN_NAMES_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if action column names form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.ACTION_COLUMN_NAMES_FORM, []);
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.PREDICTION_COLUMN_NAME_FORM ]: '',
        [ Constants.ACTION_COLUMN_NAMES_FORM ]: 'Action column names is required.',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.PREDICTION_COLUMN_NAME_FORM ]: '',
        [ Constants.ACTION_COLUMN_NAMES_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values is required.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is invalid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '-1');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.PREDICTION_COLUMN_NAME_FORM ]: '',
        [ Constants.ACTION_COLUMN_NAMES_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values should be a positive number.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if csv location form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.CSV_LOCATION_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.PREDICTION_COLUMN_NAME_FORM ]: '',
        [ Constants.ACTION_COLUMN_NAMES_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: 'CSV Location is required.',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should enable button and set form as valid if all forms are valid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.PREDICTION_COLUMN_NAME_FORM, 'Width,Length');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.ACTION_COLUMN_NAMES_FORM, ['Sex']);
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '2');
      FormHelper.clearValidatorForCsvLocation(fixture);
      FormHelper.detectChangesAndTick(fixture);

      AssertionHelper.expectValidForm(fixture);
    }));

  });


  describe('Predict Submission', () => {

    it('should call service and output result on predict success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.predict.and.returnValue(FactoryHelper.buildClassifierResponseObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([[ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierResponse(), InputEmitType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(1);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.predict).toHaveBeenCalledTimes(1);
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).not.toHaveBeenCalled();
      expect(mockService.start).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier start request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from start request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on start request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from start service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier data request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingDataRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from data request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on data request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingDataRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from data service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on predict failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.predict.and.returnValue(FactoryHelper.buildClassifierResponseErrorObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from predict');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(1);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.predict).toHaveBeenCalledTimes(1);
    }));

    it('should reset error message on predict after first predict fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.predict();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.predict.and.returnValue(FactoryHelper.buildClassifierResponseObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierResponse(), InputEmitType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(2);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.predict).toHaveBeenCalledTimes(1);
    }));

  });


  describe('Compute Predict Accuracy Submission', () => {

    it('should call service and output result on compute predict accuracy success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.computePredictAccuracy.and.returnValue(FactoryHelper.buildPredictAccuracyResponseObservable());

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([[ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildPredictAccuracyResult(), InputEmitType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(1);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.computePredictAccuracy).toHaveBeenCalledTimes(1);
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).not.toHaveBeenCalled();
      expect(mockService.start).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier start request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from start request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on start request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from start service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).not.toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier data request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingDataRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from data request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).not.toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on data request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingDataRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from data service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierRequest).not.toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on build classifier request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestService(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalled();
      expect(mockService.start).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalled();
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalled();
      expect(mockService.computePredictAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on compute predict accuracy failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.computePredictAccuracy.and.returnValue(FactoryHelper.buildPredictAccuracyResponseErrorObservable());

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from predict accuracy');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(1);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.computePredictAccuracy).toHaveBeenCalledTimes(1);
    }));

    it('should reset error message on compute predict accuracy after first predict fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingStartRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.computePredictAccuracy();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService, mockService);
      mockService.computePredictAccuracy.and.returnValue(FactoryHelper.buildPredictAccuracyResponseObservable());

      component.computePredictAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildClassifierDataRequest1(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildClassifierDataRequest2(), InputEmitType.REQUEST ],
        [ FactoryHelper.buildPredictAccuracyResult(), InputEmitType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierStartRequest).toHaveBeenCalledTimes(2);
      expect(mockService.start).toHaveBeenCalledTimes(1);
      expect(mockRequestBuilderService.buildClassifierDataRequests).toHaveBeenCalledTimes(1);
      expect(mockService.data).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildClassifierRequest).toHaveBeenCalledTimes(1);
      expect(mockService.computePredictAccuracy).toHaveBeenCalledTimes(1);
    }));

  });

});


class TestHelper {

  private constructor() { }

  static setupValidFormAndSuccessServiceCalls(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                              mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                              mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>,
                                              mockService: jasmine.SpyObj<DecisionTreeService>): void {
    FormHelper.prepareValidForm(fixture);

    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());

    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestObservable());
    mockService.start.and.returnValue(FactoryHelper.buildClassifierStartResponseObservable());

    mockRequestBuilderService.buildClassifierDataRequests.and.returnValue(FactoryHelper.buildClassifierDataRequestObservable());
    mockService.data.and.returnValue(FactoryHelper.buildDataResponseObservable());

    mockRequestBuilderService.buildClassifierRequest.and.returnValue(FactoryHelper.buildClassifierRequestObservable());
  }

  static setupValidFormAndFailingCsvReaderService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                  mockCsvReaderService: jasmine.SpyObj<CsvReaderService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvErrorObservable());
  }

  static setupValidFormAndFailingStartRequestBuilderService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                            mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                            mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestErrorObservable());
  }

  static setupValidFormAndFailingStartRequestService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                     mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                     mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>,
                                                     mockService: jasmine.SpyObj<DecisionTreeService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestObservable());
    mockService.start.and.returnValue(FactoryHelper.buildClassifierStartResponseErrorObservable());
  }

  static setupValidFormAndFailingDataRequestBuilderService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                           mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                           mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>,
                                                           mockService: jasmine.SpyObj<DecisionTreeService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestObservable());
    mockService.start.and.returnValue(FactoryHelper.buildClassifierStartResponseObservable());
    mockRequestBuilderService.buildClassifierDataRequests.and.returnValue(FactoryHelper.buildClassifierDataRequestErrorObservable());
  }

  static setupValidFormAndFailingDataRequestService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                    mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                    mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>,
                                                    mockService: jasmine.SpyObj<DecisionTreeService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestObservable());
    mockService.start.and.returnValue(FactoryHelper.buildClassifierStartResponseObservable());
    mockRequestBuilderService.buildClassifierDataRequests.and.returnValue(FactoryHelper.buildClassifierDataRequestObservable());
    mockService.data.and.returnValue(FactoryHelper.buildDataResponseErrorObservable());
  }

  static setupValidFormAndFailingRequestService(fixture: ComponentFixture<DecisionTreeInputComponent>,
                                                mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                mockRequestBuilderService: jasmine.SpyObj<ClassifierRequestBuilderService>,
                                                mockService: jasmine.SpyObj<DecisionTreeService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildClassifierStartRequest.and.returnValue(FactoryHelper.buildClassifierStartRequestObservable());
    mockService.start.and.returnValue(FactoryHelper.buildClassifierStartResponseObservable());
    mockRequestBuilderService.buildClassifierDataRequests.and.returnValue(FactoryHelper.buildClassifierDataRequestObservable());
    mockService.data.and.returnValue(FactoryHelper.buildDataResponseObservable());
    mockRequestBuilderService.buildClassifierRequest.and.returnValue(FactoryHelper.buildClassifierRequestErrorObservable());
  }
}

class FormHelper {

  static ACTUAL_EMITTED_ITEMS: [ ClassifierDataRequest | ClassifierResponse | number, InputEmitType][] = [];
  static IS_NEW_REQUEST_EMITTED = false;

  private constructor() { }

  static setupEmittedItemsSubscriber(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    FormHelper.ACTUAL_EMITTED_ITEMS = [];
    fixture.componentInstance.resultEmitter.subscribe(value => FormHelper.ACTUAL_EMITTED_ITEMS.push(value));
  }

  static setupNewRequestSubscriber(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    FormHelper.IS_NEW_REQUEST_EMITTED = false;
    fixture.componentInstance.newRequestEmitter.subscribe(() => FormHelper.IS_NEW_REQUEST_EMITTED = true);
  }

  static setValueAndMarkAsTouched(fixture: ComponentFixture<DecisionTreeInputComponent>, formName: string, value: string | string[]): void {
    const form = fixture.componentInstance.settingsForm.controls[formName];

    form.setValue(value);
    form.markAsTouched();
  }

  static detectChangesAndTick(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    fixture.detectChanges();
    tick(1000);
  }

  static clearValidatorForCsvLocation(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    // we can't set a value for file input.
    fixture.componentInstance.settingsForm.controls[Constants.CSV_LOCATION_FORM].clearValidators();
  }

  static prepareValidForm(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.PREDICTION_COLUMN_NAME_FORM, 'Date');
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.ACTION_COLUMN_NAMES_FORM, [ 'col0', 'col1' ]);
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '2');
    FormHelper.clearValidatorForCsvLocation(fixture);
    FormHelper.detectChangesAndTick(fixture);
  }
}

class AssertionHelper {

  static readonly DISABLED = 'disabled';
  static readonly TITLE = 'title';

  private constructor() { }

  static expectValidForm(fixture: ComponentFixture<DecisionTreeInputComponent>): void {
    const expectedErrorMessagePerInput = {
      [ Constants.ACTION_COLUMN_NAMES_FORM ]: '',
      [ Constants.PREDICTION_COLUMN_NAME_FORM ]: '',
      [ Constants.CSV_LOCATION_FORM ]: '',
      [ Constants.NUMBER_OF_VALUES_FORM ]: ''
    };
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectEnabledButton(fixture, Constants.PREDICT_BTN, 'Launch predict');
    AssertionHelper.expectEnabledButton(fixture, Constants.COMPUTE_PREDICT_ACCURACY_BTN, 'Launch compute predict accuracy');
    expect(fixture.componentInstance.settingsForm.valid).toBeTrue();
  }

  static expectInvalidForm(fixture: ComponentFixture<DecisionTreeInputComponent>,
                           expectedErrorMessagePerInput: { [key: string]: string }): void {
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectDisabledButton(fixture, Constants.PREDICT_BTN);
    AssertionHelper.expectDisabledButton(fixture, Constants.COMPUTE_PREDICT_ACCURACY_BTN);
    expect(fixture.componentInstance.settingsForm.valid).toBeFalse();
  }

  static expectEnabledButton(fixture: ComponentFixture<DecisionTreeInputComponent>, btnId: string, expectedTitle: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeFalse();
    expect(button.properties[AssertionHelper.TITLE]).toEqual(expectedTitle);
  }

  static expectDisabledButton(fixture: ComponentFixture<DecisionTreeInputComponent>, btnId: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeTrue();
    expect(button.properties[AssertionHelper.TITLE]).toEqual('Disabled until the form data is valid');
  }

  static assertOnEmittedItems(expectedEmittedItems: [ClassifierDataRequest | ClassifierResponse | number, InputEmitType][]): void {
    expect(FormHelper.ACTUAL_EMITTED_ITEMS).toEqual(expectedEmittedItems);
  }

}

class FactoryHelper {

  private constructor() { }

  static buildThrowExceptionIfInvalidCsvObservable(): Observable<never> {
    return of();
  }

  static buildClassifierDataRequest1(): ClassifierDataRequest {
    return new ClassifierDataRequest(1, 'col0', [ 0, 0, 1 ]);
  }

  static buildClassifierDataRequest2(): ClassifierDataRequest {
    return new ClassifierDataRequest(1, 'col1', [ 1, 1, 0 ]);
  }

  static buildClassifierResponse(): ClassifierResponse {
    return new ClassifierResponse(1, 'prediction', [ 1, 1, 1 ]);
  }

  static buildThrowExceptionIfInvalidCsvErrorObservable(): Observable<never> {
    return new Observable<never>(subscriber => {
      subscriber.error(new Error('error from csv validation'));
    });
  }

  static buildClassifierStartRequestObservable(): Observable<ClassifierStartRequest> {
    return of(new ClassifierStartRequest('prediction', [ 'col0', 'col1' ], 3));
  }

  static buildClassifierStartRequestErrorObservable(): Observable<ClassifierStartRequest> {
    return new Observable<ClassifierStartRequest>(subscriber => {
      subscriber.error(new Error('error from start request builder'));
    });
  }

  static buildClassifierStartResponseObservable(): Observable<ClassifierStartResponse> {
    return of(new ClassifierStartResponse(1));
  }

  static buildClassifierStartResponseErrorObservable(): Observable<ClassifierStartResponse> {
    return new Observable<ClassifierStartResponse>(subscriber => {
      subscriber.error(new Error('error from start service'));
    });
  }

  static buildClassifierDataRequestObservable(): Observable<ClassifierDataRequest> {
    return from([ this.buildClassifierDataRequest1(), this.buildClassifierDataRequest2() ]);
  }

  static buildClassifierDataRequestErrorObservable(): Observable<ClassifierDataRequest> {
    return new Observable<ClassifierDataRequest>(subscriber => {
      subscriber.error(new Error('error from data request builder'));
    });
  }

  static buildDataResponseObservable(): Observable<undefined> {
    return from([ undefined, undefined ]);
  }

  static buildDataResponseErrorObservable(): Observable<undefined> {
    return new Observable<undefined>(subscriber => {
      subscriber.error(new Error('error from data service'));
    });
  }

  static buildClassifierRequestObservable(): Observable<ClassifierRequest> {
    return of(new ClassifierRequest(1));
  }

  static buildClassifierRequestErrorObservable(): Observable<ClassifierRequest> {
    return new Observable<ClassifierRequest>(subscriber => {
      subscriber.error(new Error('error from request builder'));
    });
  }

  static buildClassifierResponseObservable(): Observable<ClassifierResponse> {
    return of(this.buildClassifierResponse());
  }

  static buildClassifierResponseErrorObservable(): Observable<ClassifierResponse> {
    return new Observable<ClassifierResponse>(subscriber => {
      subscriber.error(new Error('error from predict'));
    });
  }

  static buildPredictAccuracyResult(): number {
    return 98.55;
  }

  static buildPredictAccuracyResponseObservable(): Observable<number> {
    return of(this.buildPredictAccuracyResult());
  }

  static buildPredictAccuracyResponseErrorObservable(): Observable<number> {
    return new Observable<number>(subscriber => {
      subscriber.error(new Error('error from predict accuracy'));
    });
  }
}
