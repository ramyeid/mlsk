import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { Observable, of } from 'rxjs';

import { TimeSeriesAnalysisInputComponent } from './time-series-analysis-input.component';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { Constants } from '../utils/constants';

describe('TimeSeriesAnalysisInputComponent', () => {

  let component: TimeSeriesAnalysisInputComponent;
  let fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>;
  let mockService: jasmine.SpyObj<TimeSeriesAnalysisService>;
  let mockRequestBuilderService: jasmine.SpyObj<TimeSeriesRequestBuilderService>;

  beforeEach(() => {
    mockRequestBuilderService = jasmine.createSpyObj<TimeSeriesRequestBuilderService>(['buildTimeSeriesAnalysisRequest']);
    mockService = jasmine.createSpyObj<TimeSeriesAnalysisService>(['forecast']);

    TestBed.configureTestingModule({
      imports: [ ReactiveFormsModule, FormsModule ],
      declarations: [ TimeSeriesAnalysisInputComponent ],
      providers: [
        { provide: TimeSeriesAnalysisService, useValue: mockService },
        { provide: TimeSeriesRequestBuilderService, useValue: mockRequestBuilderService }
      ]
    });

    fixture = TestBed.createComponent(TimeSeriesAnalysisInputComponent);
    component = fixture.componentInstance;
    component.ngAfterViewInit();
  });

  describe('Input Validation', () => {

    it('should disable button and set form as invalid if form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      fixture.detectChanges();
      tick(1000);

      const expectedErrorMessagePerInput = { };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if date column name form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.DATE_COLUMN_NAME_FORM, '');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: 'Date column name is required. ',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if value column name form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.VALUE_COLUMN_NAME_FORM, '');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: 'Value column name is required. ', [Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if date format form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.DATE_FORMAT_FORM, '');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: 'Date format is required. ',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if date format form is invalid', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.DATE_FORMAT_FORM, '222');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: 'Date format should follow the date format pattern. ',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.NUMBER_OF_VALUES_FORM, '');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values is required. '
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if number of values form is invalid', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.NUMBER_OF_VALUES_FORM, '-1');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: 'Number of values should be a positive number. '
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if csv location form is empty', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.CSV_LOCATION_FORM, '');
      Helper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.DATE_COLUMN_NAME_FORM ]: '',
        [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: '',
        [ Constants.CSV_LOCATION_FORM ]: 'CSV Location is required. ', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
      };
      Helper.expectInvalidForm(component, button, expectedErrorMessagePerInput);
    }));

    it('should enable button and set form as value if all forms are valid', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));

      Helper.setValueAndMarkAsTouched(component, Constants.DATE_COLUMN_NAME_FORM, 'Date');
      Helper.setValueAndMarkAsTouched(component, Constants.VALUE_COLUMN_NAME_FORM, 'Passengers');
      Helper.setValueAndMarkAsTouched(component, Constants.DATE_FORMAT_FORM, 'yyyy/MM');
      Helper.setValueAndMarkAsTouched(component, Constants.NUMBER_OF_VALUES_FORM, '2');
      component.settingsForm.controls[Constants.CSV_LOCATION_FORM].clearValidators(); // we can't set a value for file input.
      Helper.detectChangesAndTick(fixture);

      Helper.expectValidForm(component, button);
    }));

  });

  describe('Forecast Submission', () => {

    it('should call service and output result on forecast success', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));
      Helper.prepareValidForm(fixture, component);
      mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(Helper.buildTimeSeriesAnalysisRequestObservable());
      mockService.forecast.and.returnValue(Helper.buildTimeSeriesObservable());
      let actualTimeSeries: TimeSeries = new TimeSeries([], 'tmp', 'tmp', 'tmp');
      component.timeSeriesResultOutput.subscribe(value => actualTimeSeries = value);

      component.submit();

      Helper.expectValidForm(component, button);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).toHaveBeenCalled();
      expect(actualTimeSeries).toEqual(Helper.buildTimeSeries());
      expect(component.errorMessage).toEqual('');
    }));

    it('should call service on and set error message on build time series analysis request failure', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));
      Helper.prepareValidForm(fixture, component);
      mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(Helper.buildTimeSeriesAnalysisRequestErrorObservable());

      component.submit();

      Helper.expectValidForm(component, button);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).not.toHaveBeenCalled();
      expect(component.errorMessage).toEqual('error from request builder');
    }));

    it('should call service and set error message on forecast failure', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));
      Helper.prepareValidForm(fixture, component);
      mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(Helper.buildTimeSeriesAnalysisRequestObservable());
      mockService.forecast.and.returnValue(Helper.buildTimeSeriesErrorObservable());

      component.submit();

      Helper.expectValidForm(component, button);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalled();
      expect(mockService.forecast).toHaveBeenCalled();
      expect(component.errorMessage).toEqual('error from service');
    }));

    it('should reset error message on forecast after first forecast fail', fakeAsync(() => {
      const button: DebugElement = fixture.debugElement.query(By.css('#SaveButton'));
      Helper.prepareValidForm(fixture, component);
      mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(Helper.buildTimeSeriesAnalysisRequestErrorObservable());
      component.submit();
      mockRequestBuilderService.buildTimeSeriesAnalysisRequest.and.returnValue(Helper.buildTimeSeriesAnalysisRequestObservable());
      mockService.forecast.and.returnValue(Helper.buildTimeSeriesObservable());

      component.submit();

      Helper.expectValidForm(component, button);
      expect(mockRequestBuilderService.buildTimeSeriesAnalysisRequest).toHaveBeenCalledTimes(2);
      expect(mockService.forecast).toHaveBeenCalledTimes(1);
      expect(component.errorMessage).toEqual('');
    }));

  });

});

class Helper {

  static readonly DISABLED = 'disabled';
  static readonly TITLE = 'title';

  static setValueAndMarkAsTouched(component: TimeSeriesAnalysisInputComponent, formName: string, value: string): void {
    const form = component.settingsForm.controls[formName];

    form.setValue(value);
    form.markAsTouched();
  }

  static detectChangesAndTick(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>): void {
    fixture.detectChanges();
    tick(1000);
  }

  static prepareValidForm(fixture: ComponentFixture<TimeSeriesAnalysisInputComponent>, component: TimeSeriesAnalysisInputComponent): void {
    Helper.setValueAndMarkAsTouched(component, Constants.DATE_COLUMN_NAME_FORM, 'Date');
    Helper.setValueAndMarkAsTouched(component, Constants.VALUE_COLUMN_NAME_FORM, 'Passengers');
    Helper.setValueAndMarkAsTouched(component, Constants.DATE_FORMAT_FORM, 'yyyy/MM');
    Helper.setValueAndMarkAsTouched(component, Constants.NUMBER_OF_VALUES_FORM, '2');
    component.settingsForm.controls[Constants.CSV_LOCATION_FORM].clearValidators(); // we can't set a value for file input.
    Helper.detectChangesAndTick(fixture);
  }

  static expectValidForm(component: TimeSeriesAnalysisInputComponent, button: DebugElement): void {
    const expectedErrorMessagePerInput = {
      [ Constants.DATE_COLUMN_NAME_FORM ]: '',
      [ Constants.VALUE_COLUMN_NAME_FORM ]: '', [Constants.DATE_FORMAT_FORM ]: '',
      [ Constants.CSV_LOCATION_FORM ]: '', [ Constants.NUMBER_OF_VALUES_FORM ]: ''
    };
    expect(component.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    expect(button.properties[Helper.DISABLED]).toBeFalse();
    expect(button.properties[Helper.TITLE]).toEqual('Save your entered data');
    expect(component.settingsForm.valid).toBeTrue();
  }

  static expectInvalidForm(component: TimeSeriesAnalysisInputComponent,
                           button: DebugElement, expectedErrorMessagePerInput: { [key: string]: string }): void {
    expect(component.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    expect(button.properties[Helper.DISABLED]).toBeTrue();
    expect(button.properties[Helper.TITLE]).toEqual('Disabled until the form data is valid');
    expect(component.settingsForm.valid).toBeFalse();
  }

  static buildTimeSeriesAnalysisRequestObservable(): Observable<TimeSeriesAnalysisRequest> {
    const row1 = new TimeSeriesRow('1', 1);
    const row2 = new TimeSeriesRow('2', 2);
    const row3 = new TimeSeriesRow('3', 3);
    const rows = [row1, row2, row3];
    const timeSeries = new TimeSeries(rows, 'Date', 'Value', 'yyyyMM');
    return of(new TimeSeriesAnalysisRequest(3, timeSeries));
  }

  static buildTimeSeriesAnalysisRequestErrorObservable(): Observable<TimeSeriesAnalysisRequest> {
    return new Observable<TimeSeriesAnalysisRequest>(observer => {
      observer.error(new Error('error from request builder'));
    });
  }

  static buildTimeSeries(): TimeSeries {
    const row1 = new TimeSeriesRow('4', 4);
    const row2 = new TimeSeriesRow('5', 5);
    const row3 = new TimeSeriesRow('6', 6);
    const rows = [row1, row2, row3];
    return new TimeSeries(rows, 'Date', 'Value', 'yyyyMM');
  }

  static buildTimeSeriesObservable(): Observable<TimeSeries> {
    return of(Helper.buildTimeSeries());
  }

  static buildTimeSeriesErrorObservable(): Observable<TimeSeries> {
    return new Observable<TimeSeries>(observer => {
      observer.error(new Error('error from service'));
    });
  }
}
