import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';

import { ValidationMessageGenerator } from './validation-message-generator';
import { ValidationMessages } from './validation-messages';

describe('ValidationMessageGenerator', () => {

  let form: FormGroup;
  let validationMessageGenerator: ValidationMessageGenerator;

  beforeEach(() => {
    form = Helper.buildForm();
    const validationMessages = Helper.buildValidationMessages();
    validationMessageGenerator = new ValidationMessageGenerator(validationMessages);
  });

  it('should return errors according to forms error', () => {
    Helper.markAsDirtyAndAddErrors(Helper.FORM1, { min: 1 });
    Helper.markAsTouchedAndAddErrors(Helper.FORM2, { invalid: 1, required: 1 });
    Helper.markAsDirtyAndAddErrors(Helper.FORM3, { min: 1, invalid: 1, required: 1 });

    const actualResult = validationMessageGenerator.generateErrorMessages(form);

    const expectedResult = {
      formName1: 'formName1MinErrorMessage',
      formName2: 'formName2InvalidErrorMessage formName2RequiredErrorMessage',
      formName3: 'formName3MinErrorMessage formName3InvalidErrorMessage formName3RequiredErrorMessage'
    };
    expect(actualResult).toEqual(expectedResult);
  });

  it('should return errors according to form errors and validation message', () => {
    Helper.markAsDirtyAndAddErrors(Helper.FORM1, { invalid: 1 });
    Helper.markAsTouchedAndAddErrors(Helper.FORM2, { min: 1 });
    Helper.markAsTouchedAndAddErrors(Helper.FORM3, { min: 1 });

    const actualResult = validationMessageGenerator.generateErrorMessages(form);

    const expectedResult = {
      formName1: '',
      formName2: '',
      formName3: 'formName3MinErrorMessage'
    };
    expect(actualResult).toEqual(expectedResult);
  });

  it('should not return any error if forms were not modified', () => {

    const actualResult = validationMessageGenerator.generateErrorMessages(form);

    const expectedResult = {
      formName1: '',
      formName2: '',
      formName3: ''
    };
    expect(actualResult).toEqual(expectedResult);
  });

  it('should not return any error if forms were not modified but contain errors', () => {
    Helper.addErrors(Helper.FORM1, { min: 1 });
    Helper.addErrors(Helper.FORM2, { invalid: 1, required: 1 });
    Helper.addErrors(Helper.FORM3, { min: 1, invalid: 1, required: 1 });

    const actualResult = validationMessageGenerator.generateErrorMessages(form);

    const expectedResult = {
      formName1: '',
      formName2: '',
      formName3: ''
    };
    expect(actualResult).toEqual(expectedResult);
  });

});


class Helper {

  static FORM1: FormControl;
  static FORM2: FormControl;
  static FORM3: FormControl;

  static buildValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(
      'formName1',
      {
        minMessage: 'formName1MinErrorMessage'
      }
    );
    validationMessages.push(
      'formName2',
      {
        invalidMessage: 'formName2InvalidErrorMessage',
        requiredMessage: 'formName2RequiredErrorMessage'
      }
    );
    validationMessages.push(
      'formName3',
      {
        invalidMessage: 'formName3InvalidErrorMessage',
        requiredMessage: 'formName3RequiredErrorMessage',
        minMessage: 'formName3MinErrorMessage'
      }
    );

    return validationMessages;
  }

  static markAsTouchedAndAddErrors(form: FormControl, errors: ValidationErrors): void {
    form.markAsTouched();
    this.addErrors(form, errors);
  }

  static markAsDirtyAndAddErrors(form: FormControl, errors: ValidationErrors): void {
    form.markAsDirty();
    this.addErrors(form, errors);
  }

  static addErrors(form: FormControl, errors: ValidationErrors): void {
    form.setErrors(errors);
  }

  static buildForm(): FormGroup {
    Helper.FORM1 = new FormControl();
    Helper.FORM2 = new FormControl();
    Helper.FORM3 = new FormControl();
    return new FormGroup({
      formName1: Helper.FORM1,
      subFormGroup: new FormGroup({
        formName2: Helper.FORM2,
        formName3: Helper.FORM3
      })
    });
  }
}
