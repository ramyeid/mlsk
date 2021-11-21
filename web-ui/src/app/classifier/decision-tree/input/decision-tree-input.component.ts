import { Component, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { last, tap, switchMap, debounceTime } from 'rxjs/operators';

import { ValidationMessageGenerator } from 'src/app/shared/validator/message-generator/validation-message-generator';
import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';
import { DecisionTreeService } from '../service/decision-tree.service';
import { ClassifierRequestBuilderService } from '../request-builder/classifier-request-builder.service';
import { DecisionTreeValidationMessages } from '../utils/decision-tree-validation-messages';
import { Constants } from '../utils/constants';
import { ClassifierRequest } from '../model/classifier-request';
import { ClassifierDataResponse } from '../model/classifier-data-response';
import { ClassifierStartResponse } from '../model/classifier-start-response';
import { ClassifierDataRequest } from '../model/classifier-data-request';
import { ClassifierEmittedType } from '../model/classifier-emitted-type';

@Component({
  selector: 'mlsk-decision-tree-input',
  templateUrl: './decision-tree-input.component.html',
  styleUrls: ['./decision-tree-input.component.css']
})
export class DecisionTreeInputComponent implements AfterViewInit {

  @Output() resultEmitter = new EventEmitter<[ ClassifierDataRequest | ClassifierDataResponse | number, ClassifierEmittedType]>();
  @Output() newRequestEmitter = new EventEmitter<undefined>();
  private readonly requestBuilder: ClassifierRequestBuilderService;
  private readonly service: DecisionTreeService;
  private readonly csvReaderService: CsvReaderService;
  private readonly validationMessageGenrator: ValidationMessageGenerator;
  settingsForm: FormGroup;
  errorMessage: string;
  errorMessagePerInput: { [key: string]: string } = {};
  private csvFile: File;
  isWaitingForResult: boolean;

  constructor(formBuilder: FormBuilder,
              requestBuilder: ClassifierRequestBuilderService,
              service: DecisionTreeService,
              csvReaderService: CsvReaderService) {
    this.requestBuilder = requestBuilder;
    this.service = service;
    this.csvReaderService = csvReaderService;
    const validationMessages = DecisionTreeValidationMessages.buildDecisionTreeValidationMessages();
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

  computePredictAccuracy(): void {
    this.postNewRequest(request => this.service.computePredictAccuracy(request));
  }

  onUpload(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files?.length) {
      this.csvFile = input.files[0];
    }
  }

  private postNewRequest(serviceCall: (request: ClassifierRequest) => Observable<ClassifierDataResponse | number>): void {
    const predictionColumnName: string = this.settingsForm.get(Constants.PREDICTION_COLUMN_NAME_FORM)?.value;
    const actionColumnNames: string[] = this.settingsForm.get(Constants.ACTION_COLUMN_NAMES_FORM)?.value;
    const numberOfValues: number = this.settingsForm.get(Constants.NUMBER_OF_VALUES_FORM)?.value;
    let requestId = '';

    this.onNewRequest();

    this.csvReaderService.throwExceptionIfInvalidCsv(this.csvFile, [predictionColumnName, ...actionColumnNames])
      .pipe(
        last(null, 'ignored'),
        switchMap(() => this.requestBuilder.buildClassifierStartRequest(predictionColumnName, actionColumnNames, numberOfValues)),
        switchMap(startRequest => this.service.start(startRequest)),
        tap((value: ClassifierStartResponse) => requestId = value.requestId ),
        switchMap(() => this.requestBuilder.buildClassifierDataRequests(this.csvFile, predictionColumnName, actionColumnNames, requestId)),
        switchMap(classifierDataRequest => {
          this.resultEmitter.emit([classifierDataRequest, ClassifierEmittedType.REQUEST]);
          return this.service.data(classifierDataRequest);
        }),
        last(null, 'ignored'),
        switchMap(() => this.requestBuilder.buildClassifierRequest(requestId)),
        switchMap(request => serviceCall(request))
      ).subscribe({
        next: result => this.onSuccess(result),
        error: err => this.onError(err)
      });
  }

  private onNewRequest(): void {
    this.errorMessage = '';
    this.newRequestEmitter.emit();
    this.isWaitingForResult = true;
  }

  private onSuccess(result: ClassifierDataResponse | number): void {
    this.resultEmitter.emit([result, ClassifierEmittedType.RESULT]);
    this.isWaitingForResult = false;
  }

  private onError(errorMessage: Error): void {
    this.errorMessage = errorMessage.message;
    this.isWaitingForResult = false;
  }

  private buildFormGroup(): { [key: string]: [string, ValidationErrors[]] } {
    return {
      [ Constants.PREDICTION_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.ACTION_COLUMN_NAMES_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    };
  }
}
