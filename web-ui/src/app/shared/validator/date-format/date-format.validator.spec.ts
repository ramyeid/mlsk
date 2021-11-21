import { FormControl } from '@angular/forms';

import { DateFormatValidator } from './date-format.validator';

describe('DateFormatValidator', () => {

  it('should return invalid if control if value contains number', () => {
    const value = 'yyy22MM';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return invalid if value does not contain any pattern', () => {
    const value = 'abcdef';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return invalid if value contains unsupported seperator', () => {
    const value = 'yyyy=MM=dd';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return null if value is valid date format with yyyyMMdd', () => {
    const value = 'yyyyMMdd';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with dd.MM.yyyy', () => {
    const value = 'dd.MM.yyyy';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with dd.MM.yyyy-HH:mm:ss.SSS', () => {
    const value = 'dd.MM.yyyy-HH:mm:ss.SSS';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with MM/dd/yy HH:mm:ss.SSS', () => {
    const value = 'MM/dd/yy HH:mm:ss.SSS';
    const control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

});
