import { FormGroup } from '@angular/forms';


export class ValidationMessageGenerator {

  private readonly validationMessages: { [key: string]: { [key: string]: string } };

  constructor(validationMessages: { [key: string]: { [key: string]: string } }) {
    this.validationMessages = validationMessages;
  }

  generateErrorMessages(container: FormGroup): { [key: string]: string } {
    const messages: { [key: string]: string} = {};
    for (const controlKey in container.controls) {
      if (container.controls.hasOwnProperty(controlKey)) {
        const control = container.controls[controlKey];
        if (control instanceof FormGroup) {
          const containerErrorMessages = this.generateErrorMessages(control);
          Object.assign(messages, containerErrorMessages);
        } else {
          if (this.validationMessages[controlKey]) {
            messages[controlKey] = '';
            if ((control.dirty || control.touched) && control.errors) {
              Object.keys(control.errors).map(messageKey => {
                if (this.validationMessages[controlKey][messageKey]) {
                  messages[controlKey] += this.validationMessages[controlKey][messageKey] + ' ';
                }
              });
            }
          }
        }
      }
    }
    return messages;
  }

}
