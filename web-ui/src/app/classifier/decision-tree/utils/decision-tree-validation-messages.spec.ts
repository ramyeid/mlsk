import { DecisionTreeValidationMessages } from './decision-tree-validation-messages';
import { ValidationMessages } from 'src/app/shared/validation-messages';
import { Constants } from './constants';

describe('DecisionTreeValidationMessages', () => {

  it('should build correct validation messages', () => {
    const expectedValidationMessage = Helper.buildExpectedValidationMessages();

    const actualValidationMessages = DecisionTreeValidationMessages.buildDecisionTreeValidationMessages();

    expect(actualValidationMessages).toEqual(expectedValidationMessage);
  });

});


class Helper {

  private constructor() { }

  static buildExpectedValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(Constants.PREDICTION_COLUMN_NAME_FORM, { requiredMessage: 'Prediction column name is required.' });
    validationMessages.push(Constants.ACTION_COLUMN_NAMES_FORM, { requiredMessage: 'Action column names is required.' });
    validationMessages.push(Constants.NUMBER_OF_VALUES_FORM, { requiredMessage: 'Number of values is required.', minMessage: 'Number of values should be a positive number.' });
    validationMessages.push(Constants.CSV_LOCATION_FORM, { requiredMessage: 'CSV Location is required.' });

    return validationMessages;
  }
}
