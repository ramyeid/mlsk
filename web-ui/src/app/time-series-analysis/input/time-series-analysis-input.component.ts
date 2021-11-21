import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, last, switchMap } from 'rxjs/operators';

import { DateFormatValidator } from 'src/app/shared/validator/date-format/date-format.validator';
import { ValidationMessageGenerator } from 'src/app/shared/validator/message-generator/validation-message-generator';
import { TimeSeriesAnalysisValidationMessages } from '../utils/time-series-analysis-validation-messages';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeries } from '../model/time-series';
import { Constants } from '../utils/constants';
import { TimeSeriesEmittedType } from '../model/time-series-emitted-type';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';
import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';

@Component({
  selector: 'mlsk-time-series-analysis-input',
  templateUrl: './time-series-analysis-input.component.html',
  styleUrls: ['./time-series-analysis-input.component.css']
})
export class TimeSeriesAnalysisInputComponent implements AfterViewInit {

  @Output() resultEmitter = new EventEmitter<[TimeSeries | number, TimeSeriesEmittedType]>();
  @Output() newRequestEmitter = new EventEmitter<undefined>();
  private readonly requestBuilder: TimeSeriesRequestBuilderService;
  private readonly service: TimeSeriesAnalysisService;
  private readonly csvReaderService: CsvReaderService;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  private csvFile: File;
  isWaitingForResult: boolean;

  constructor(formBuilder: FormBuilder,
              requestBuilder: TimeSeriesRequestBuilderService,
              service: TimeSeriesAnalysisService,
              csvReaderService: CsvReaderService) {
    this.requestBuilder = requestBuilder;
    this.service = service;
    this.csvReaderService = csvReaderService;
    const validationMessages = TimeSeriesAnalysisValidationMessages.buildTimeSeriesValidationMessages();
    this.validationMessageGenrator = new ValidationMessageGenerator(validationMessages);
    this.isWaitingForResult = false;
    this.settingsForm = formBuilder.group(this.buildFormGroup());
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

    this.csvReaderService.throwExceptionIfInvalidCsv(this.csvFile, [dateColumnName, valueColumnName])
      .pipe(
        last(null, 'ignored'),
        switchMap(() => this.requestBuilder.buildTimeSeriesAnalysisRequest(this.csvFile, dateColumnName, valueColumnName, dateFormat, numberOfValues)),
        switchMap(request => {
          this.resultEmitter.emit([request.timeSeries, TimeSeriesEmittedType.REQUEST]);
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

  private onSuccess(result: TimeSeries | number): void {
    this.resultEmitter.emit([result, TimeSeriesEmittedType.RESULT]);
    this.isWaitingForResult = false;
  }

  private onError(errorMessage: Error): void {
    this.errorMessage = errorMessage.message;
    this.isWaitingForResult = false;
  }

  private buildFormGroup(): { [key: string]: [string, ValidationErrors[]] } {
    return {
      [ Constants.DATE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.VALUE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.DATE_FORMAT_FORM ]: [ '', [ Validators.required, DateFormatValidator.validateDateFormat ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    };
  }
}
