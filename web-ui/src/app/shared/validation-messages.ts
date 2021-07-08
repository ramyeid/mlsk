export class ValidationMessages {

  private readonly validationMessages: ValidationMessage[] = [];

  push(formName: string, messagePerError: Partial<MessagePerError>): void {
    if (!this.has(formName)) {
      this.validationMessages.push(new ValidationMessage(formName, messagePerError));
    }
  }

  has(formName: string): boolean {
    return this.validationMessages
            .find(validationMessage => validationMessage.formName === formName) !== undefined;
  }

  hasError(formName: string, error: string): boolean {
    return this.validationMessages
            .find(validationMessage =>
                validationMessage.formName === formName && validationMessage.messagePerError[error] !== undefined
            ) !== undefined;
  }

  getError(formName: string, error: string): string | undefined {
    return this.validationMessages
      .find(validationMessage =>
          validationMessage.formName === formName && validationMessage.messagePerError[error] !== undefined
      )?.messagePerError[error];
  }
}

class ValidationMessage {

  readonly formName: string;
  readonly messagePerError: { [key: string]: string | undefined };

  constructor(formName: string, messagePerError: Partial<MessagePerError>) {
    this.formName = formName;
    this.messagePerError = {
      required: messagePerError.requiredMessage,
      invalid: messagePerError.invalidMessage,
      min: messagePerError. minMessage
    };
  }
}

interface MessagePerError {

  readonly requiredMessage: string | undefined;
  readonly invalidMessage: string | undefined;
  readonly minMessage: string | undefined;
}
