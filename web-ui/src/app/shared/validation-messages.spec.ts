import { ValidationMessages } from './validation-messages';

describe('ValidationMessages', () => {

  describe('push', () => {

    it('should not add form if already exists', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      validationMessages.push('formName1', { minMessage: 'newMinErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName1', 'min');
      expect(actualErrorMessage).toEqual('minErrorMessage');
    });

  });

  describe('getError', () => {

    it('should push and find validation message for form with required', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { requiredMessage: 'requiredErrorMessage' });
      validationMessages.push('formName2', { minMessage: 'minErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName1', 'required');

      expect(actualErrorMessage).toEqual('requiredErrorMessage');
    });

    it('should push and find validation message for form with min', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { requiredMessage: 'requiredErrorMessage' });
      validationMessages.push('formName2', { minMessage: 'minErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName2', 'min');

      expect(actualErrorMessage).toEqual('minErrorMessage');
    });

    it('should push and find validation message for form with invalid', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { requiredMessage: 'requiredErrorMessage' });
      validationMessages.push('formName2', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName3', { requiredMessage: 'requiredErrorMessage', minMessage: 'minErrorMessage', invalidMessage: 'invalidErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName3', 'invalid');

      expect(actualErrorMessage).toEqual('invalidErrorMessage');
    });

    it('should return undefined if form not available', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName3', 'min');

      expect(actualErrorMessage).toBeUndefined();
    });

    it('should return undefined if error not available', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualErrorMessage = validationMessages.getError('formName2', 'unavailableError');

      expect(actualErrorMessage).toBeUndefined();
    });

  });

  describe('has', () => {

    it('should return true if contains form', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualResult = validationMessages.has('formName2');

      expect(actualResult).toBeTrue();
    });

    it('should return false if does not contain form', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualResult = validationMessages.has('formName3');

      expect(actualResult).toBeFalse();
    });

  });

  describe('hasError', () => {

    it('should return true if contains form and error', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualResult = validationMessages.hasError('formName2', 'invalid');

      expect(actualResult).toBeTrue();
    });

    it('should return false if does not contain form', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualResult = validationMessages.hasError('formName3', 'invalid');

      expect(actualResult).toBeFalse();
    });

    it('should return false if does not contain error', () => {
      const validationMessages = new ValidationMessages();
      validationMessages.push('formName1', { minMessage: 'minErrorMessage' });
      validationMessages.push('formName2', { invalidMessage: 'invalidErrorMessage' });

      const actualResult = validationMessages.hasError('formName2', 'min');

      expect(actualResult).toBeFalse();
    });

  });

});
