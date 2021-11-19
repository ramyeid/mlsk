import { Component, AfterViewInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { debounceTime } from 'rxjs';

import { ValidationMessageGenerator } from 'src/app/shared/validation-message-generator';
import { DecisionTreeValidationMessages } from '../utils/decision-tree-validation-messages';
import { Constants } from '../utils/constants';

@Component({
  selector: 'app-decision-tree-input',
  templateUrl: './decision-tree-input.component.html',
  styleUrls: ['./decision-tree-input.component.css']
})
export class DecisionTreeInputComponent implements AfterViewInit {

  private readonly formBuilder: FormBuilder;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  private csvFile: File;
  isWaitingForResult: boolean;


  constructor(formBuilder: FormBuilder) {
    this.formBuilder = formBuilder;
    const validationMessages = DecisionTreeValidationMessages.buildDecisionTreeValidationMessages();
    this.validationMessageGenrator = new ValidationMessageGenerator(validationMessages);
    this.isWaitingForResult = false;
    this.settingsForm = this.buildForm();
  }

  ngAfterViewInit(): void {
    this.settingsForm.valueChanges.pipe(
      debounceTime(800)
    ).subscribe(() => {
      this.errorMessagePerInput = this.validationMessageGenrator.generateErrorMessages(this.settingsForm);
    });
  }

  predict(): void {
  }

  computePredictAccuracy(): void {
  }

  onUpload(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files?.length) {
      this.csvFile = input.files[0];
    }
  }

  private buildForm(): FormGroup {
    return this.formBuilder.group({
      [ Constants.PREDICTION_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.ACTION_COLUMN_NAMES_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    });
  }
}
