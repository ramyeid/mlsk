import { AfterViewInit, EventEmitter, Injectable } from '@angular/core';
import { FormBuilder, FormGroup, ValidationErrors } from '@angular/forms';
import { debounceTime } from 'rxjs';
import { CsvReaderService } from '../../csv/csv-reader.service';

import { InputEmitType } from '../../model/input-emit-type';
import { ValidationMessageGenerator } from '../../validator/message-generator/validation-message-generator';
import { ValidationMessages } from '../../validator/message-generator/validation-messages';

@Injectable()
export abstract class AbstractInputComponent<Request, Result> implements AfterViewInit {

  private _resultEmitter: EventEmitter<[ Request | Result, InputEmitType ]>;
  private _newRequestEmitter: EventEmitter<undefined>;
  protected readonly csvReaderService: CsvReaderService;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  protected csvFile: File;
  isWaitingForResult: boolean;

  constructor(formBuilder: FormBuilder,
              csvReaderService: CsvReaderService,
              validationMessages: ValidationMessages) {
    this.csvReaderService = csvReaderService;
    this.validationMessageGenrator = new ValidationMessageGenerator(validationMessages);
    this.isWaitingForResult = false;
    this.settingsForm = formBuilder.group(this.buildFormGroup());
  }

  protected abstract buildFormGroup(): { [key: string]: [string, ValidationErrors[]] };

  protected abstract setEmitters(): void;

  ngAfterViewInit(): void {
    this.settingsForm.valueChanges.pipe(
      debounceTime(800)
    ).subscribe(() => {
      this.errorMessagePerInput = this.validationMessageGenrator.generateErrorMessages(this.settingsForm);
    });
    this.setEmitters();
  }

  protected setResultEmitter(resultEmitter: EventEmitter<[ Request | Result, InputEmitType ]>): void {
    this._resultEmitter = resultEmitter;
  }

  protected  setNewRequestEmitter(newRequestEmitter: EventEmitter<undefined>): void {
    this._newRequestEmitter = newRequestEmitter;
  }

  onUpload(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files?.length) {
      this.csvFile = input.files[0];
    }
  }

  protected emitRequest(request: Request): void {
    this._resultEmitter.emit([request, InputEmitType.REQUEST]);
  }

  protected onNewRequest(): void {
    this.errorMessage = '';
    this._newRequestEmitter.emit();
    this.isWaitingForResult = true;
  }

  protected onSuccess(result: Result): void {
    this._resultEmitter.emit([result, InputEmitType.RESULT]);
    this.isWaitingForResult = false;
  }

  protected onError(errorMessage: Error): void {
    this.errorMessage = errorMessage.message;
    this.isWaitingForResult = false;
  }
}
