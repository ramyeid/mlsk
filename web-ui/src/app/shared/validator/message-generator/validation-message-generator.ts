import { FormControl, FormGroup } from '@angular/forms';

import { ValidationMessages } from './validation-messages';

export class ValidationMessageGenerator {

  private readonly validationMessages: ValidationMessages;

  constructor(validationMessages: ValidationMessages) {
    this.validationMessages = validationMessages;
  }

  generateErrorMessages(container: FormGroup): { [key: string]: string } {
    const messages: { [key: string]: string} = {};
    for (const controlKey in container.controls) {
      const control = container.controls[controlKey];
      if (control instanceof FormGroup) {
        Object.assign(messages, this.generateErrorMessages(control));
      } else if (control instanceof FormControl) {
        if (this.validationMessages.has(controlKey)) {
          messages[controlKey] = this.retrieveErrorMessages(control, controlKey);
        }
      }
    }
    return messages;
  }

  private retrieveErrorMessages(control: FormControl, controlKey: string): string {
    if (this.isDirtyOrTouched(control) && control.errors) {
      return Object.keys(control.errors)
        .map(messageKey => this.retrieveErrorMessage(controlKey, messageKey))
        .filter(errorMessage => errorMessage && errorMessage.length != 0)
        .join(' ');
    }

    return '';
  }

  private retrieveErrorMessage(controlKey: string, messageKey: string): string | undefined {
    return this.validationMessages.hasError(controlKey, messageKey) ?
      this.validationMessages.getError(controlKey, messageKey) : undefined;
  }

  private isDirtyOrTouched(control: FormControl): boolean {
    return control.dirty || control.touched;
  }
}
