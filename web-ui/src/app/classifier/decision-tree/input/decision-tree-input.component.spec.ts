import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

import { DecisionTreeInputComponent } from './decision-tree-input.component';
import { Constants } from '../utils/constants';
import { InputListComponent } from 'src/app/shared/component/input-list/input-list.component';

describe('DecisionTreeInputComponent', () => {

  let component: DecisionTreeInputComponent;
  let fixture: ComponentFixture<DecisionTreeInputComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ ReactiveFormsModule, FormsModule, MatIconModule ],
      declarations: [ InputListComponent, DecisionTreeInputComponent ]
    });

    fixture = TestBed.createComponent(DecisionTreeInputComponent);
    component = fixture.componentInstance;
    component.ngAfterViewInit();
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

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.ACTION_COLUMN_NAMES_FORM, '');
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

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.ACTION_COLUMN_NAMES_FORM, 'Sex');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.PREDICTION_COLUMN_NAME_FORM, 'Width,Length');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.NUMBER_OF_VALUES_FORM, '2');
      FormHelper.clearValidatorForCsvLocation(fixture);
      FormHelper.detectChangesAndTick(fixture);

      AssertionHelper.expectValidForm(fixture);
    }));

  });

});

class FormHelper {

  private constructor() { }

  static setValueAndMarkAsTouched(fixture: ComponentFixture<DecisionTreeInputComponent>, formName: string, value: string): void {
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
}
