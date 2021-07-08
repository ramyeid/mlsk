import { ValidationMessages } from 'src/app/shared/validation-messages';

export class TimeSeriesAnalysisValidationMessages {

  private constructor() {
  }

  static buildTimeSeriesValidationMessages(): ValidationMessages {
    const validationMessages = new ValidationMessages();

    validationMessages.push('dateColumnName', { requiredMessage: 'Date column name is required.' });
    validationMessages.push('valueColumnName', { requiredMessage: 'Value column name is required.' });
    validationMessages.push('dateFormat', { requiredMessage: 'Date format is required.', invalidMessage: 'Date format should follow the date format pattern.' });
    validationMessages.push('numberOfValues', { requiredMessage: 'Number of values is required.', minMessage: 'Number of values should be a positive number.' });
    validationMessages.push('csvLocation', { requiredMessage: 'CSV Location is required.' });

    return validationMessages;
  }
}
