import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ConfigurationComponent } from './configuration.component';
import { ConfigurationService } from '../service/configuration.service';
import { Constants } from '../utils/constants';

describe('ConfigurationComponent', () => {

  let component: ConfigurationComponent;
  let fixture: ComponentFixture<ConfigurationComponent>;
  let mockService: jasmine.SpyObj<ConfigurationService>;

  beforeEach(() => {
    mockService = jasmine.createSpyObj<ConfigurationService>(['getServerHost', 'getServerPort', 'saveConfiguration']);

    mockService.getServerHost.and.returnValue('localhost');
    mockService.getServerPort.and.returnValue(8080);

    TestBed.configureTestingModule({
      imports: [ ReactiveFormsModule, FormsModule, MatIconModule ],
      declarations: [ ConfigurationComponent ],
      providers: [
        { provide: ConfigurationService, useValue: mockService }
      ]
    });

    fixture = TestBed.createComponent(ConfigurationComponent);
    component = fixture.componentInstance;
    component.ngAfterViewInit();
  });


  describe('Input Validation', () => {

    it('should enable buttons and set form as valid on startup', fakeAsync(() => {

      fixture.detectChanges();
      tick(1000);

      AssertionHelper.expectValidFormOnStartup(fixture);
    }));

    it('should disable button and set form as invalid if server host form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_HOST_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.SERVER_HOST_FORM ]: 'Server Host is required.',
        [ Constants.SERVER_PORT_FORM ]: ''
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if server port form is empty', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_PORT_FORM, '');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.SERVER_HOST_FORM ]: '',
        [ Constants.SERVER_PORT_FORM ]: 'Server Port is required.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should disable button and set form as invalid if server port form is invalid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_PORT_FORM, '-1');
      FormHelper.detectChangesAndTick(fixture);

      const expectedErrorMessagePerInput = {
        [ Constants.SERVER_HOST_FORM ]: '',
        [ Constants.SERVER_PORT_FORM ]: 'Server Port should be a positive number.'
      };
      AssertionHelper.expectInvalidForm(fixture, expectedErrorMessagePerInput);
    }));

    it('should enable button and set form as valid if all forms are valid', fakeAsync(() => {

      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_HOST_FORM, 'myLocalHost');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_PORT_FORM, '123123');
      FormHelper.detectChangesAndTick(fixture);

      AssertionHelper.expectValidForm(fixture);
    }));

  });


  describe('Save', () => {

    it('should call service on save', fakeAsync(() => {
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_HOST_FORM, 'myLocalHost');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_PORT_FORM, '123123');
      FormHelper.detectChangesAndTick(fixture);

      component.save();

      const expectedConfiguration = {
        [ Constants.SERVER_HOST_FORM ]: 'myLocalHost',
        [ Constants.SERVER_PORT_FORM ]: '123123'
      };
      expect(mockService.saveConfiguration).toHaveBeenCalledWith(expectedConfiguration);
    }));

  });

  describe('Cancel', () => {

    it('should restore values on cancel', fakeAsync(() => {
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_HOST_FORM, 'myLocalHost');
      FormHelper.setValueAndMarkAsTouched(fixture, Constants.SERVER_PORT_FORM, '123123');
      FormHelper.detectChangesAndTick(fixture);

      component.cancel();
      FormHelper.detectChangesAndTick(fixture);

      AssertionHelper.expectValidForm(fixture);
      AssertionHelper.expectFormValue(fixture, Constants.SERVER_HOST_FORM, 'localhost');
      AssertionHelper.expectFormValue(fixture, Constants.SERVER_PORT_FORM, 8080);
    }));

  });

});

class FormHelper {

  static setValueAndMarkAsTouched(fixture: ComponentFixture<ConfigurationComponent>, formName: string, value: string): void {
    const form = fixture.componentInstance.configurationForm.controls[formName];

    form.setValue(value);
    form.markAsTouched();
  }

  static detectChangesAndTick(fixture: ComponentFixture<ConfigurationComponent>): void {
    fixture.detectChanges();
    tick(1000);
  }
}

class AssertionHelper {

  static readonly DISABLED = 'disabled';
  static readonly TITLE = 'title';

  static expectFormValue(fixture: ComponentFixture<ConfigurationComponent>, formName: string, expectedValue: string | number): void {
    const actualValue = fixture.componentInstance.configurationForm.controls[formName].value;
    expect(actualValue).toBe(expectedValue);
  }

  static expectValidFormOnStartup(fixture: ComponentFixture<ConfigurationComponent>): void {
    const expectedErrorMessagePerInput = { };
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectEnabledButton(fixture, Constants.SAVE_BTN, 'Save');
    AssertionHelper.expectEnabledButton(fixture, Constants.CANCEL_BTN, 'Cancel');
    expect(fixture.componentInstance.configurationForm.valid).toBeTrue();
  }

  static expectValidForm(fixture: ComponentFixture<ConfigurationComponent>): void {
    const expectedErrorMessagePerInput = {
      [ Constants.SERVER_HOST_FORM ]: '',
      [ Constants.SERVER_PORT_FORM ]: ''
    };
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectEnabledButton(fixture, Constants.SAVE_BTN, 'Save');
    AssertionHelper.expectEnabledButton(fixture, Constants.CANCEL_BTN, 'Cancel');
    expect(fixture.componentInstance.configurationForm.valid).toBeTrue();
  }

  static expectInvalidForm(fixture: ComponentFixture<ConfigurationComponent>,
                           expectedErrorMessagePerInput: { [key: string]: string }): void {
    expect(fixture.componentInstance.errorMessagePerInput).toEqual(expectedErrorMessagePerInput);
    AssertionHelper.expectDisabledButton(fixture, Constants.SAVE_BTN);
    AssertionHelper.expectDisabledButton(fixture, Constants.CANCEL_BTN);
    expect(fixture.componentInstance.configurationForm.valid).toBeFalse();
  }

  static expectEnabledButton(fixture: ComponentFixture<ConfigurationComponent>, btnId: string, expectedTitle: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeFalse();
    expect(button.properties[AssertionHelper.TITLE]).toEqual(expectedTitle);
  }

  static expectDisabledButton(fixture: ComponentFixture<ConfigurationComponent>, btnId: string): void {
    const button: DebugElement = fixture.debugElement.query(By.css(`#${btnId}`));

    expect(button.properties[AssertionHelper.DISABLED]).toBeTrue();
    expect(button.properties[AssertionHelper.TITLE]).toEqual('Disabled until the form is valid');
  }
}
