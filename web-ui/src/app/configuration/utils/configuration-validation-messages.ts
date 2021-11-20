import { ValidationMessages } from 'src/app/shared/validator/message-generator/validation-messages';
import { Constants } from './constants';

export class ConfigurationValidationMessages {

  private constructor() {
  }

  static buildConfigurationValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(Constants.SERVER_HOST_FORM, { requiredMessage: 'Server Host is required.' });
    validationMessages.push(Constants.SERVER_PORT_FORM, { requiredMessage: 'Server Port is required.', minMessage: 'Server Port should be a positive number.'});

    return validationMessages;
  }
}
