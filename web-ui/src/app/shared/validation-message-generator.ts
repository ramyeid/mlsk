import { FormGroup } from '@angular/forms';

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
        const containerErrorMessages = this.generateErrorMessages(control);
        Object.assign(messages, containerErrorMessages);
      } else {
        if (this.validationMessages.has(controlKey)) {
          messages[controlKey] = '';
          if ((control.dirty || control.touched) && control.errors) {
            Object.keys(control.errors).forEach(messageKey => {
              if (this.validationMessages.hasError(controlKey, messageKey)) {
                messages[controlKey] += `${this.validationMessages.getError(controlKey, messageKey)} `;
              }
            });
          }
        }
      }
    }
    return messages;
  }

}
