import { ConfigurationValidationMessages } from './configuration-validation-messages';
import { ValidationMessages } from 'src/app/shared/validation-messages';
import { Constants } from './constants';

describe('ConfigurationValidationMessages', () => {

  it('should build correct validation messages', () => {
    const expectedValidationMessage = Helper.buildExpectedValidationMessages();

    const actualValidationMessages = ConfigurationValidationMessages.buildConfigurationValidationMessages();

    expect(actualValidationMessages).toEqual(expectedValidationMessage);
  });

});


class Helper {

  private constructor() { }

  static buildExpectedValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(Constants.SERVER_HOST_FORM, { requiredMessage: 'Server Host is required.' });
    validationMessages.push(Constants.SERVER_PORT_FORM, { requiredMessage: 'Server Port is required.', minMessage: 'Server Port should be a positive number.' });

    return validationMessages;
  }
}
