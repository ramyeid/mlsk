import { Component, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, Validators, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { last, switchMap } from 'rxjs/operators';

import { AbstractInputComponent } from 'src/app/shared/component/input/abstract-input.component';
import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { DateFormatValidator } from 'src/app/shared/validator/date-format/date-format.validator';
import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';
import { TimeSeriesAnalysisValidationMessages } from '../utils/time-series-analysis-validation-messages';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeries } from '../model/time-series';
import { Constants } from '../utils/constants';
import { TimeSeriesAnalysisRequest } from '../model/time-series-analysis-request';

@Component({
  selector: 'mlsk-time-series-analysis-input',
  templateUrl: './time-series-analysis-input.component.html',
  styleUrls: ['./time-series-analysis-input.component.css']
})
export class TimeSeriesAnalysisInputComponent extends AbstractInputComponent<TimeSeries, TimeSeries | number> {

  @Output() resultEmitter = new EventEmitter<[TimeSeries | number,  InputEmitType]>();
  @Output() newRequestEmitter = new EventEmitter<undefined>();
  private readonly requestBuilder: TimeSeriesRequestBuilderService;
  private readonly service: TimeSeriesAnalysisService;

  constructor(formBuilder: UntypedFormBuilder,
              requestBuilder: TimeSeriesRequestBuilderService,
              service: TimeSeriesAnalysisService,
              csvReaderService: CsvReaderService) {
    super(formBuilder, csvReaderService, TimeSeriesAnalysisValidationMessages.buildTimeSeriesValidationMessages());
    this.requestBuilder = requestBuilder;
    this.service = service;
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
          this.emitRequest(request.timeSeries);
          return serviceCall(request);
        })
      ).subscribe({
        next: (result: TimeSeries | number) => this.onSuccess(result),
        error: err => this.onError(err)
      });
  }

  override setEmitters(): void {
    this.setResultEmitter(this.resultEmitter);
    this.setNewRequestEmitter(this.newRequestEmitter);
  }

  override buildFormGroup(): { [key: string]: [string, ValidationErrors[]] } {
    return {
      [ Constants.DATE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.VALUE_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.DATE_FORMAT_FORM ]: [ '', [ Validators.required, DateFormatValidator.validateDateFormat ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    };
  }
}
