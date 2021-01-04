import { Component, OnInit, AfterViewInit, ViewChildren, ElementRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControlName } from '@angular/forms';

import { Observable, fromEvent, merge } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

import { DateFormatValidator } from '../../shared/date-format.validator';
import { ValidationMessages } from '../../shared/validation-messages';
import { ValidationMessageGenerator } from './../../shared/validation-message-generator';
import { TimeSeriesRequestBuilderService } from '../request-builder/time-series-request-builder.service';
import { TimeSeriesAnalysisService } from '../service/time-series-analysis.service';
import { TimeSeries } from '../model/time-series';

@Component({
  selector: 'app-time-series-analysis-input',
  templateUrl: './time-series-analysis-input.component.html',
  styleUrls: ['./time-series-analysis-input.component.css']
})
export class TimeSeriesAnalysisInputComponent implements OnInit, AfterViewInit {

  @ViewChildren(FormControlName, { read: ElementRef }) formInputElements: ElementRef[];
  private readonly formBuilder: FormBuilder;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  private readonly requestBuilder: TimeSeriesRequestBuilderService;
  private readonly service: TimeSeriesAnalysisService;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  private csvFile: File;
  isWaitingForResult: boolean;

  constructor(formBuilder: FormBuilder,
              timeSeriesRequestBuilderService: TimeSeriesRequestBuilderService,
              timeServiceAnalysisService: TimeSeriesAnalysisService) {
    this.formBuilder = formBuilder;
    this.requestBuilder = timeSeriesRequestBuilderService;
    this.service = timeServiceAnalysisService;
    this.validationMessageGenrator = new ValidationMessageGenerator(ValidationMessages.getTimeSeriesValidationMessages());
    this.isWaitingForResult = false;
  }

  ngOnInit(): void {
    this.settingsForm = this.formBuilder.group({
      dateColumnName: ['', [Validators.required]],
      valueColumnName: ['', [Validators.required]],
      dateFormat: ['', [Validators.required, DateFormatValidator.validateDateFormat]],
      csvLocation: ['', [Validators.required]],
      numberOfValues: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngAfterViewInit(): void {
    const controlBlurs: Observable<any>[] = this.formInputElements
    .map((formControl: ElementRef) => fromEvent(formControl.nativeElement, 'blur'));

    merge(this.settingsForm.valueChanges, ...controlBlurs).pipe(
      debounceTime(800)
    ).subscribe(() => {
      this.errorMessagePerInput = this.validationMessageGenrator.generateErrorMessages(this.settingsForm);
    });
  }

  submit(): void {
    this.isWaitingForResult = true;
    const dateColumnName: string = this.settingsForm.get('dateColumnName')?.value;
    const valueColumnName: string = this.settingsForm.get('valueColumnName')?.value;
    const dateFormat: string = this.settingsForm.get('dateFormat')?.value;
    const numberOfValues: number = this.settingsForm.get('numberOfValues')?.value;

    this.requestBuilder
          .buildTimeSeriesAnalysisRequest(this.csvFile, dateColumnName, valueColumnName, dateFormat, numberOfValues)
          .pipe(
            switchMap(output => this.service.forecast(output))
          ).subscribe({
            next: (timeSeries: TimeSeries) => this.onSuccess(timeSeries),
            error: err => this.onError(err)
          });
  }

  private onSuccess(timeSeriesResult: TimeSeries): void {
    console.log(`output: ${JSON.stringify(timeSeriesResult)}`);
    this.isWaitingForResult = false;
  }

  private onError(errorMessage: string): void {
    this.errorMessage = errorMessage;
    this.isWaitingForResult = false;
  }

  onUpload(event: any): void {
    const files: File[] = event.target.files;
    this.csvFile = files[0];
  }
}
