import { FormControl } from '@angular/forms';

import { DateFormatValidator } from './date-format.validator';

describe('DateFormatValidator', () => {

  it('should return invalid if control if value contains number', () => {
    let value = "yyy22MM";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return invalid if value does not contain any pattern', () => {
    let value = "abcdef";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return invalid if value contains unsupported seperator', () => {
    let value = "yyyy=MM=dd";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toEqual({invalid: true });
  });

  it('should return null if value is valid date format with yyyyMMdd', () => {
    let value = "yyyyMMdd";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with dd.MM.yyyy', () => {
    let value = "dd.MM.yyyy";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with dd.MM.yyyy-HH:mm:ss.SSS', () => {
    let value = "dd.MM.yyyy-HH:mm:ss.SSS";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

  it('should return null if value is valid date format with MM/dd/yy HH:mm:ss.SSS', () => {
    let value = "MM/dd/yy HH:mm:ss.SSS";
    let control = new FormControl(value);

    const actualResult = DateFormatValidator.validateDateFormat(control);

    expect(actualResult).toBeNull();
  });

});
