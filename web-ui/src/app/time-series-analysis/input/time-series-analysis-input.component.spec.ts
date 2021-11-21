import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { Observable, of } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';

import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';
import { TimeSeriesAnalysisInputComponent } from './time-series-analysis-input.component';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { Constants } from '../utils/constants';
import { TimeSeriesEmittedType } from '../model/time-series-emitted-type';

describe('TimeSeriesAnalysisInputComponent', () => {

  let component: TimeSeriesAnalysisInputComponent;
  let fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>;
  let mockService: jasmine.SpyObj<TimeSeriesAnalysisService>;
  let mockRequestBuilderService: jasmine.SpyObj<TimeSeriesRequestBuilderService>;
  let mockCsvReaderService: jasmine.SpyObj<CsvReaderService>;

  beforeEach(() => {
    mockService = jasmine.createSpyObj<TimeSeriesAnalysisService>(['forecast', 'predict', 'forecastVsActual', 'computeForecastAccuracy']);
    mockRequestBuilderService = jasmine.createSpyObj<TimeSeriesRequestBuilderService>(['buildTimeSeriesAnalysisRequest']);
    mockCsvReaderService = jasmine.createSpyObj<CsvReaderService>(['throwExceptionIfInvalidCsv']);

    TestBed.configureTestingModule({
      imports: [ ReactiveFormsModule, FormsModule, MatIconModule ],
      declarations: [ TimeSeriesAnalysisInputComponent ],
      providers: [
        { provide: TimeSeriesAnalysisService, useValue: mockService },
        { provide: TimeSeriesRequestBuilderService, useValue: mockRequestBuilderService },
        { provide: CsvReaderService, useValue: mockCsvReaderService }
      ]
    });

    fixture = TestBed.createComponent(TimeSeriesAnalysisInputComponent);
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

    it('should disable button and set form as invalid if date column name form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_COLUMN_NAME_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: 'Date column name is required.',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if value column name form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.VALUE_COLUMN_NAME_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: 'Value column name is required.',
        [ Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if date format form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_FORMAT_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: 'Date format is required.',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if date format form is invalid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_FORMAT_FORM, '222');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: 'Date format should follow the date format pattern.',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values is required.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is invalid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '-1');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '',
        [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values should be a positive number.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if csv location form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.CSV_LOCATION_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
        [ Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: 'CSV Location is required.',
        [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should enable button and set form as valid if all forms are valid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_COLUMN_NAME_FORM, 'Date');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.VALUE_COLUMN_NAME_FORM, 'Passengers');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_FORMAT_FORM, 'yyyy/MM');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '2');
      FormHelper.clearValidatorForCsvLocation(fixture);
      FormHelper.detectChangesAndTick(fixture);

      AssertionHelper.expectValidForm(fixture);
    }));

  });


  describe('Forecast Submission', () => {

    it('should call service and output result on forecast success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecast.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.forecast();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).toHaveBeenCalled();
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.forecast();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).not.toHaveBeenCalled();
      expect(mockService.forecast).not.toHaveBeenCalled();
    }));

    it('should set error message on build time series analysis request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.forecast();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).not.toHaveBeenCalled();
    }));

    it('should call service and set error message on forecast failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecast.and.returnValue(FactoryHelper.buildTimeSeriesErrorObservable());

      component.forecast();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).toHaveBeenCalled();
    }));

    it('should reset error message on forecast after first forecast fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.forecast();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecast.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.forecast();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalledTimes(2);
      expect(mockService.forecast).toHaveBeenCalledTimes(1);
    }));

  });


  describe('Predict Submission', () => {

    it('should call service and output result on predict success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.predict.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.predict).toHaveBeenCalled();
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).not.toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should set error message on build time series analysis request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.predict).not.toHaveBeenCalled();
    }));

    it('should call service and set error message on predict failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.predict.and.returnValue(FactoryHelper.buildTimeSeriesErrorObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.predict).toHaveBeenCalled();
    }));

    it('should reset error message on predict after first predict fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.predict();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.predict.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.predict();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalledTimes(2);
      expect(mockService.predict).toHaveBeenCalledTimes(1);
    }));

  });


  describe('Forecast Vs Actual Submission', () => {

    it('should call service and output result on forecast vs actual success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecastVsActual.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.forecastVsActual();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecastVsActual).toHaveBeenCalled();
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.forecastVsActual();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).not.toHaveBeenCalled();
      expect(mockService.forecastVsActual).not.toHaveBeenCalled();
    }));

    it('should set error message on build time series analysis request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.forecastVsActual();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecastVsActual).not.toHaveBeenCalled();
    }));

    it('should call service and set error message on forecast vs actual failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecastVsActual.and.returnValue(FactoryHelper.buildTimeSeriesErrorObservable());

      component.forecastVsActual();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecastVsActual).toHaveBeenCalled();
    }));

    it('should reset error message on forecast vs actual after first forecast vs actual fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.forecastVsActual();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.forecastVsActual.and.returnValue(FactoryHelper.buildTimeSeriesObservable());

      component.forecastVsActual();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildTimeSeriesResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalledTimes(2);
      expect(mockService.forecastVsActual).toHaveBeenCalledTimes(1);
    }));

  });


  describe('Compute Forecast Accuracy Submission', () => {

    it('should call service and output result on compute forecast accuracy success', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.computeForecastAccuracy.and.returnValue(FactoryHelper.buildAccuracyResultObserable());

      component.computeForecastAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildAccuracyResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.computeForecastAccuracy).toHaveBeenCalled();
    }));

    it('should set error message on csv validation failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingCsvReaderService(fixture, mockCsvReaderService);

      component.computeForecastAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from csv validation');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).not.toHaveBeenCalled();
      expect(mockService.computeForecastAccuracy).not.toHaveBeenCalled();
    }));

    it('should set error message on build time series analysis request failure', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);

      component.computeForecastAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from request builder');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.computeForecastAccuracy).not.toHaveBeenCalled();
    }));

    it('should call service and set error message on compute forecast accuracy failure', fakeAsync(() => {
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.computeForecastAccuracy.and.returnValue(FactoryHelper.buildAccuracyErrorObservable());

      component.computeForecastAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('error from service');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalled();
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.computeForecastAccuracy).toHaveBeenCalled();
    }));

    it('should reset error message on compute forecast accuracy after first compute forecast accuracy fail', fakeAsync(() => {
      TestHelper.setupValidFormAndFailingRequestBuilderService(fixture, mockCsvReaderService, mockRequestBuilderService);
      component.computeForecastAccuracy();
      TestHelper.setupValidFormAndSuccessServiceCalls(fixture, mockCsvReaderService, mockRequestBuilderService);
      mockService.computeForecastAccuracy.and.returnValue(FactoryHelper.buildAccuracyResultObserable());

      component.computeForecastAccuracy();

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.assertOnEmittedItems([ [ FactoryHelper.buildTimeSeriesRequest(), TimeSeriesEmittedType.REQUEST ], [ FactoryHelper.buildAccuracyResult(), TimeSeriesEmittedType.RESULT ] ]);
      expect(FormHelper.IS_NEW_REQUEST_EMITTED).toBeTrue();
      expect(component.errorMessage).toEqual('');
      expect(mockCsvReaderService.throwExceptionIfInvalidCsv).toHaveBeenCalledTimes(2);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalledTimes(2);
      expect(mockService.computeForecastAccuracy).toHaveBeenCalledTimes(1);
    }));

  });

});


class TestHelper {

  private constructor() { }

  static setupValidFormAndSuccessServiceCalls(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>,
                                              mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                              mockRequestBuilderService: jasmine.SpyObj<TimeSeriesRequestBuilderService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(FactoryHelper.buildTimeSeriesAnalysisRequestObservable());
  }

  static setupValidFormAndFailingCsvReaderService(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>,
                                                  mockCsvReaderService: jasmine.SpyObj<CsvReaderService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvErrorObservable());
  }

  static setupValidFormAndFailingRequestBuilderService(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>,
                                                       mockCsvReaderService: jasmine.SpyObj<CsvReaderService>,
                                                       mockRequestBuilderService: jasmine.SpyObj<TimeSeriesRequestBuilderService>): void {
    FormHelper.prepareValidForm(fixture);
    mockCsvReaderService.throwExceptionIfInvalidCsv.and.returnValue(FactoryHelper.buildThrowExceptionIfInvalidCsvObservable());
    mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(FactoryHelper.buildTimeSeriesAnalysisRequestErrorObservable());
  }
}

class FormHelper {

  static ACTUAL_EMITTED_ITEMS: [TimeSeries | number, TimeSeriesEmittedType][] = [];
  static IS_NEW_REQUEST_EMITTED = false;

  private constructor() { }

  static setupEmittedItemsSubscriber(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    FormHelper.ACTUAL_EMITTED_ITEMS = [];
    fixture.componentInstance.resultEmitter.subscribe(value => FormHelper.ACTUAL_EMITTED_ITEMS.push(value));
  }

  static setupNewRequestSubscriber(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    FormHelper.IS_NEW_REQUEST_EMITTED = false;
    fixture.componentInstance.newRequestEmitter.subscribe(() => FormHelper.IS_NEW_REQUEST_EMITTED = true);
  }

  static setValueAndMarkAsTouched(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>, formName: string, value: string): void {
    const form = fixture.componentInstance.settingsForm.controls[formName];

    form.setValue(value);
    form.markAsTouched();
  }

  static detectChangesAndTick(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    fixture.detectChanges();
    tick(1000);
  }

  static clearValidatorForCsvLocation(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    // we can't set a value for file input.
    fixture.componentInstance.settingsForm.controls[Constants.CSV_LOCATION_FORM].clearValidators();
  }

  static prepareValidForm(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_COLUMN_NAME_FORM, 'Date');
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.VALUE_COLUMN_NAME_FORM, 'Passengers');
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.DATE_FORMAT_FORM, 'yyyy/MM');
    FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '2');
    FormHelper.clearValidatorForCsvLocation(fixture);
    FormHelper.detectChangesAndTick(fixture);
  }
}

class AssertionHelper {
  static readonly DISABLED = 'disabled';
  static readonly TITLE = 'title';

  private constructor() { }

  static expectValidForm(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    const expectedErrorMessagePerInput = {
      [ Constants.DATE_COLUMN_NAME_FORM ]: '',
      [ Constants.VALUE_COLUMN_NAME_FORM ]: '',
      [ Constants.DATE_FORMAT_FORM ]: '',
      [ Constants.CSV_LOCATION_FORM ]: '',
      [ Constants.NUMBER_OF_VALUES_FORM ]: ''
    };
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectEnabledButton(fixture, Constants.PREDICT_BTN, 'Launch predict');
    AssertionHelper.expectEnabledButton(fixture, Constants.FORECAST_BTN, 'Launch forecast');
    AssertionHelper.expectEnabledButton(fixture, Constants.FORECAST_VS_ACTUAL_BTN, 'Launch forecast vs actual');
    AssertionHelper.expectEnabledButton(fixture, Constants.COMPUTE_FORECAST_ACCURACY_BTN, 'Launch compute forecast accuracy');
    expect(fixture.componentInstance.settingsForm.valid).toBeTrue();
  }

  static expectInvalidForm(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>,
                           expectedErrorMessagePerInput: { [key: string]: string }): void {
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectDisabledButton(fixture, Constants.PREDICT_BTN);
    AssertionHelper.expectDisabledButton(fixture, Constants.FORECAST_BTN);
    AssertionHelper.expectDisabledButton(fixture, Constants.FORECAST_VS_ACTUAL_BTN);
    AssertionHelper.expectDisabledButton(fixture, Constants.COMPUTE_FORECAST_ACCURACY_BTN);
    expect(fixture.componentInstance.settingsForm.valid).toBeFalse();
  }

  static expectEnabledButton(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>, btnId: string, expectedTitle: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeFalse();
    expect(button.properties[AssertionHelper.TITLE]).toEqual(expectedTitle);
  }

  static expectDisabledButton(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>, btnId: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeTrue();
    expect(button.properties[AssertionHelper.TITLE]).toEqual('Disabled until the form data is valid');
  }

  static assertOnEmittedItems(expectedEmittedItems: [TimeSeries | number, TimeSeriesEmittedType][]): void {
    expect(FormHelper.ACTUAL_EMITTED_ITEMS).toEqual(expectedEmittedItems);
  }

}

class FactoryHelper {

  private constructor() { }

  static buildTimeSeriesRequest(): TimeSeries {
    const row1 = new TimeSeriesRow('1', 1);
    const row2 = new TimeSeriesRow('2', 2);
    const row3 = new TimeSeriesRow('3', 3);
    const rows = [row1, row2, row3];
    return new TimeSeries(rows, 'Date', 'Value', 'yyyyMM');
  }

  static buildThrowExceptionIfInvalidCsvObservable(): Observable<undefined> {
    return of();
  }

  static buildThrowExceptionIfInvalidCsvErrorObservable(): Observable<undefined> {
    return new Observable<undefined>(subscriber => {
      subscriber.error(new Error('error from csv validation'));
    });
  }

  static buildTimeSeriesAnalysisRequestObservable(): Observable<TimeSeriesAnalysisRequest> {
    return of(new TimeSeriesAnalysisRequest(3, FactoryHelper.buildTimeSeriesRequest()));
  }

  static buildTimeSeriesAnalysisRequestErrorObservable(): Observable<TimeSeriesAnalysisRequest> {
    return new Observable<TimeSeriesAnalysisRequest>(subscriber => {
      subscriber.error(new Error('error from request builder'));
    });
  }

  static buildTimeSeriesResult(): TimeSeries {
    const row1 = new TimeSeriesRow('4', 4);
    const row2 = new TimeSeriesRow('5', 5);
    const row3 = new TimeSeriesRow('6', 6);
    const rows = [row1, row2, row3];
    return new TimeSeries(rows, 'Date', 'Value', 'yyyyMM');
  }

  static buildTimeSeriesObservable(): Observable<TimeSeries> {
    return of(FactoryHelper.buildTimeSeriesResult());
  }

  static buildTimeSeriesErrorObservable(): Observable<TimeSeries> {
    return new Observable<TimeSeries>(subscriber => {
      subscriber.error(new Error('error from service'));
    });
  }

  static buildAccuracyResult(): number {
    return 98.55;
  }

  static buildAccuracyResultObserable(): Observable<number> {
    return of(FactoryHelper.buildAccuracyResult());
  }

  static buildAccuracyErrorObservable(): Observable<number> {
    return new Observable<number>(subscriber => {
      subscriber.error(new Error('error from service'));
    });
  }

}
