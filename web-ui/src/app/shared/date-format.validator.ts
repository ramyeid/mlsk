import { AbstractControl } from '@angular/forms';

export class DateFormatValidator {

  // Order of these two array is important!
  private static readonly DATE_FORMAT_PATTERNS: string[] = ['yyyy', 'yy', 'MM', 'dd', 'HH', 'hh', 'mm', 'ss', 'SSS'];
  private static readonly DATE_FORMAT_SEPARATOR: string[] = ['/', '\\', '.', '-', ':', ',', ' '];

  private constructor() { }

  static validateDateFormat(control: AbstractControl): { [key: string]: boolean } | null {
    if (control.value && !DateFormatValidator.isValidDateFormat(control.value)) {
      return { invalid: true };
    }
    return null;
  }

  private static isValidDateFormat(dateFormat: string): boolean {
    // A date format is valid if
    //  1. it does not contain any number
    //  2. it contains one of the patterns
    //  3. when all the possible patterns and seperators are removed no alphabet is left in the string
    return !DateFormatValidator.doesContainNumber(dateFormat) &&
           DateFormatValidator.doesContainPattern(dateFormat) &&
           DateFormatValidator.isEmptyAfterRemovingPatternAndSeperator(dateFormat);
  }

  private static doesContainNumber(dateFormat: string): boolean {
    return /[0-9]/g.test(dateFormat);
  }

  private static doesContainPattern(dateFormat: string): boolean {
    return DateFormatValidator.DATE_FORMAT_PATTERNS.some(pattern => {
      return dateFormat.includes(pattern);
    });
  }

  private static isEmptyAfterRemovingPatternAndSeperator(dateFormat: string): boolean {
    let dateFormatWithoutPattern = dateFormat;

    DateFormatValidator.DATE_FORMAT_PATTERNS.forEach(pattern => {
      dateFormatWithoutPattern = dateFormatWithoutPattern.replace(pattern, '');
    });

    DateFormatValidator.DATE_FORMAT_SEPARATOR.forEach(separator => {
      dateFormatWithoutPattern = dateFormatWithoutPattern.split(separator).join('');
    });

    return dateFormatWithoutPattern.length === 0;
  }
}
