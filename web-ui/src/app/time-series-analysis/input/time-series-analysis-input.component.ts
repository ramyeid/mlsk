import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

import { debounceTime, switchMap } from 'rxjs/operators';

import { DateFormatValidator } from '../../shared/date-format.validator';
import { ValidationMessageGenerator } from './../../shared/validation-message-generator';
import { TimeSeriesAnalysisValidationMessages } from '../utils/time-series-analysis-validation-messages';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeries } from '../model/time-series';
import { Constants } from '../utils/constants';

@Component({
  selector: 'app-time-series-analysis-input',
  templateUrl: './time-series-analysis-input.component.html',
  styleUrls: ['./time-series-analysis-input.component.css']
})
export class TimeSeriesAnalysisInputComponent implements AfterViewInit {

  @Output() timeSeriesResultOutput = new EventEmitter<TimeSeries>();
  private readonly formBuilder: FormBuilder;
  private readonly requestBuilder: TimeSeriesRequestBuilderService;
  private readonly service: TimeSeriesAnalysisService;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  private csvFile: File;
  isWaitingForResult: boolean;

  constructor(formBuilder: FormBuilder,
              requestBuilder: TimeSeriesRequestBuilderService,
              service: TimeSeriesAnalysisService) {
    this.formBuilder = formBuilder;
    this.requestBuilder = requestBuilder;
    this.service = service;
    const validationMessages = TimeSeriesAnalysisValidationMessages.buildTimeSeriesValidationMessages();
    this.validationMessageGenrator = new ValidationMessageGenerator(validationMessages);
    this.isWaitingForResult = false;
    this.buildForm();
  }

  ngAfterViewInit(): void {
    this.settingsForm.valueChanges.pipe(
      debounceTime(800)
    ).subscribe(() => {
      this.errorMessagePerInput = this.validationMessageGenrator.generateErrorMessages(this.settingsForm);
    });
  }

  submit(): void {
    this.errorMessage = '';
    this.isWaitingForResult = true;
    const dateColumnName: string = this.settingsForm.get(Constants.DATE_COLUMN_NAME_FORM)?.value;
    const valueColumnName: string = this.settingsForm.get(Constants.VALUE_COLUMN_NAME_FORM)?.value;
    const dateFormat: string = this.settingsForm.get(Constants.DATE_FORMAT_FORM)?.value;
    const numberOfValues: number = this.settingsForm.get(Constants.NUMBER_OF_VALUES_FORM)?.value;

    this.requestBuilder
          .buildTimeSeriesAnalysisRequest(this.csvFile, dateColumnName, valueColumnName, dateFormat, numberOfValues)
          .pipe(
            switchMap(output => this.service.forecast(output))
          ).subscribe({
            next: (timeSeries: TimeSeries) => this.onSuccess(timeSeries),
            error: err => this.onError(err)
          });
  }

  onUpload(event: any): void {
    const files: File[] = event.target.files;
    this.csvFile = files[0];
  }

  private buildForm(): void {
    this.settingsForm = this.formBuilder.group({
      [ Constants.DATE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.VALUE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.DATE_FORMAT_FORM ]: [ '', [ Validators.required, DateFormatValidator.validateDateFormat ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    });
  }

  private onSuccess(timeSeriesResult: TimeSeries): void {
    this.timeSeriesResultOutput.emit(timeSeriesResult);
    console.log(`output: ${JSON.stringify(timeSeriesResult)}`);
    this.isWaitingForResult = false;
  }

  private onError(errorMessage: Error): void {
    this.errorMessage = errorMessage.message;
    this.isWaitingForResult = false;
  }
}
