export class ValidationMessages {

  private constructor() {
  }

  static getTimeSeriesValidationMessages(): { [key: string]: { [key: string]: string}}  {
    return {
      dateColumnName: {
        required: 'Date column name is required.',
      },
      valueColumnName: {
        required: 'Value column name is required.',
      },
      dateFormat: {
        required: 'Date format is required.',
        invalid: 'Date format should follow the date format pattern'
      },
      numberOfValues: {
        required: 'Number of values is required.',
        min: 'Number of values should be a positive number'
      },
      csvLocation: {
        required: 'CSV Location is required.',
      }
   };
  }
}
