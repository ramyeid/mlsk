import { TimeSeriesAnalysisValidationMessages } from './time-series-analysis-validation-messages';
import { ValidationMessages } from 'src/app/shared/validation-messages';
import { Constants } from './constants';

describe('TimeSeriesAnalysisValidationMessages', () => {

  it('should build correct validation messages', () => {
    const expectedValidationMessage = Helper.buildExpectedValidationMessages();

    const actualValidationMessages = TimeSeriesAnalysisValidationMessages.buildTimeSeriesValidationMessages();

    expect(actualValidationMessages).toEqual(expectedValidationMessage);
  });

});


class Helper {

  private constructor() { }

  static buildExpectedValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push(Constants.DATE_COLUMN_NAME_FORM, { requiredMessage: 'Date column name is required.' });
    validationMessages.push(Constants.VALUE_COLUMN_NAME_FORM, { requiredMessage: 'Value column name is required.' });
    validationMessages.push(Constants.DATE_FORMAT_FORM, { requiredMessage: 'Date format is required.', invalidMessage: 'Date format should follow the date format pattern.' });
    validationMessages.push(Constants.NUMBER_OF_VALUES_FORM, { requiredMessage: 'Number of values is required.', minMessage: 'Number of values should be a positive number.' });
    validationMessages.push(Constants.CSV_LOCATION_FORM, { requiredMessage: 'CSV Location is required.' });

    return validationMessages;
  }
}
