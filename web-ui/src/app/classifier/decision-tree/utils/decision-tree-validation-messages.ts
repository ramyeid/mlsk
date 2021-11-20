import { ValidationMessages } from 'src/app/shared/validator/message-generator/validation-messages';
import { Constants } from './constants';

export class DecisionTreeValidationMessages {

  private constructor() {
  }

  static buildDecisionTreeValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(Constants.PREDICTION_COLUMN_NAME_FORM, { requiredMessage: 'Prediction column name is required.' });
    validationMessages.push(Constants.ACTION_COLUMN_NAMES_FORM, { requiredMessage: 'Action column names is required.' });
    validationMessages.push(Constants.NUMBER_OF_VALUES_FORM, { requiredMessage: 'Number of values is required.', minMessage: 'Number of values should be a positive number.' });
    validationMessages.push(Constants.CSV_LOCATION_FORM, { requiredMessage: 'CSV Location is required.' });

    return validationMessages;
  }
}
