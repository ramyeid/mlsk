import { Component, AfterViewInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { debounceTime } from 'rxjs/operators';

import { ValidationMessageGenerator } from 'src/app/shared/validator/message-generator/validation-message-generator';
import { ConfigurationValidationMessages } from '../utils/configuration-validation-messages';
import { Constants } from '../utils/constants';
import { ConfigurationService } from '../service/configuration.service';

@Component({
  selector: 'mlsk-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements AfterViewInit {

  private readonly formBuilder: FormBuilder;
  private readonly service: ConfigurationService;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  configurationForm: FormGroup;
  errorMessagePerInput: { [key: string]: string } = {};

  constructor(formBuilder: FormBuilder, service: ConfigurationService) {
    this.formBuilder = formBuilder;
    this.service = service;
    const validationMessages = ConfigurationValidationMessages.buildConfigurationValidationMessages();
    this.validationMessageGenrator = new ValidationMessageGenerator(validationMessages);
    this.configurationForm = this.buildForm();
    this.resetValues();
  }

  ngAfterViewInit(): void {
    this.configurationForm.valueChanges.pipe(
      debounceTime(800)
    ).subscribe(() => {
      this.errorMessagePerInput = this.validationMessageGenrator.generateErrorMessages(this.configurationForm);
    });
  }

  save(): void {
    this.service.saveConfiguration(this.configurationForm.getRawValue());
  }

  cancel(): void {
    this.resetValues();
  }

  private resetValues(): void {
    this.configurationForm.patchValue({
      [ Constants.SERVER_HOST_FORM ]: this.service.getServerHost(),
      [ Constants.SERVER_PORT_FORM ]: this.service.getServerPort()
    });
  }

  private buildForm(): FormGroup {
    return this.formBuilder.group({
      [ Constants.SERVER_HOST_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.SERVER_PORT_FORM ]: [ '', [ Validators.required, Validators.min(0) ] ]
    });
  }
}
