import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

import { DateFormatValidator } from 'src/app/shared/date-format.validator';
import { ValidationMessageGenerator } from 'src/app/shared/validation-message-generator';
import { TimeSeriesAnalysisValidationMessages } from '../utils/time-series-analysis-validation-messages';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeries } from '../model/time-series';
import { Constants } from '../utils/constants';
import { TimeSeriesType } from '../model/time-series-type';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';

@Component({
  selector: 'app-time-series-analysis-input',
  templateUrl: './time-series-analysis-input.component.html',
  styleUrls: ['./time-series-analysis-input.component.css']
})
export class TimeSeriesAnalysisInputComponent implements AfterViewInit {

  @Output() resultEmitter = new EventEmitter<[TimeSeries | number, TimeSeriesType]>();
  @Output() newRequestEmitter = new EventEmitter<undefined>();
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
    this.postNewRequest(request => this.service.predict(request));
  }

  forecast(): void {
    this.postNewRequest(request => this.service.forecast(request));
  }

  forecastVsActual(): void {
    this.postNewRequest(request => this.service.forecastVsActual(request));
  }

  computeForecastAccuracy(): void {
    this.postNewRequest(request => this.service.computeForecastAccuracy(request));
  }

  onUpload(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files?.length) {
      this.csvFile = input.files[0];
    }
  }

  private postNewRequest(serviceCall: (request: TimeSeriesAnalysisRequest) => Observable<TimeSeries | number>): void {
    const dateColumnName: string = this.settingsForm.get(Constants.DATE_COLUMN_NAME_FORM)?.value;
    const valueColumnName: string = this.settingsForm.get(Constants.VALUE_COLUMN_NAME_FORM)?.value;
    const dateFormat: string = this.settingsForm.get(Constants.DATE_FORMAT_FORM)?.value;
    const numberOfValues: number = this.settingsForm.get(Constants.NUMBER_OF_VALUES_FORM)?.value;

    this.onNewRequest();

    this.requestBuilder
      .buildTimeSeriesAnalysisRequest(this.csvFile, dateColumnName, valueColumnName, dateFormat, numberOfValues)
      .pipe(
        switchMap(request => {
          this.resultEmitter.emit([request.timeSeries, TimeSeriesType.REQUEST]);
          return serviceCall(request);
        })
      ).subscribe({
        next: (result: TimeSeries | number) => this.onSuccess(result),
        error: err => this.onError(err)
      });
  }

  private onNewRequest(): void {
    this.errorMessage = '';
    this.newRequestEmitter.emit();
    this.isWaitingForResult = true;
  }

  private buildForm(): FormGroup {
    return this.formBuilder.group({
      [ Constants.DATE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.VALUE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.DATE_FORMAT_FORM ]: [ '', [ Validators.required, DateFormatValidator.validateDateFormat ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    });
  }

  private onSuccess(result: TimeSeries | number): void {
    this.resultEmitter.emit([result, TimeSeriesType.RESULT]);
    this.isWaitingForResult = false;
  }

  private onError(errorMessage: Error): void {
    this.errorMessage = errorMessage.message;
    this.isWaitingForResult = false;
  }
}
