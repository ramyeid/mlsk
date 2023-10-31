import { Component, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, Validators, ValidationErrors } from '@angular/forms';
import { Observable } from 'rxjs';
import { last, tap, switchMap } from 'rxjs/operators';

import { AbstractInputComponent } from 'src/app/shared/component/input/abstract-input.component';
import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { CsvReaderService } from 'src/app/shared/csv/csv-reader.service';
import { DecisionTreeService } from '../service/decision-tree.service';
import { ClassifierRequestBuilderService } from '../../request-builder/classifier-request-builder.service';
import { DecisionTreeValidationMessages } from '../utils/decision-tree-validation-messages';
import { Constants } from '../utils/constants';
import { ClassifierRequest } from '../../model/classifier-request';
import { ClassifierDataResponse } from '../../model/classifier-data-response';
import { ClassifierStartResponse } from '../../model/classifier-start-response';
import { ClassifierDataRequest } from '../../model/classifier-data-request';

@Component({
  selector: 'mlsk-decision-tree-input',
  templateUrl: './decision-tree-input.component.html',
  styleUrls: ['./decision-tree-input.component.css']
})
export class DecisionTreeInputComponent extends AbstractInputComponent<ClassifierDataRequest, ClassifierDataResponse | number> {

  @Output() resultEmitter = new EventEmitter<[ClassifierDataRequest | ClassifierDataResponse | number,  InputEmitType]>();
  @Output() newRequestEmitter = new EventEmitter<undefined>();
  private readonly requestBuilder: ClassifierRequestBuilderService;
  private readonly service: DecisionTreeService;

  constructor(formBuilder: UntypedFormBuilder,
              requestBuilder: ClassifierRequestBuilderService,
              service: DecisionTreeService,
              csvReaderService: CsvReaderService) {
    super(formBuilder, csvReaderService, DecisionTreeValidationMessages.buildDecisionTreeValidationMessages());
    this.service = service;
    this.requestBuilder = requestBuilder;
  }

  predict(): void {
    this.postNewRequest(request => this.service.predict(request));
  }

  computePredictAccuracy(): void {
    this.postNewRequest(request => this.service.computePredictAccuracy(request));
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
          this.emitRequest(classifierDataRequest);
          return this.service.data(classifierDataRequest);
        }),
        last(null, 'ignored'),
        switchMap(() => this.requestBuilder.buildClassifierRequest(requestId)),
        switchMap(request => serviceCall(request))
      ).subscribe({
        next: (result: ClassifierDataResponse | number) => this.onSuccess(result),
        error: err => this.onError(err)
      });
  }

  override setEmitters(): void {
    this.setResultEmitter(this.resultEmitter);
    this.setNewRequestEmitter(this.newRequestEmitter);
  }

  override buildFormGroup(): { [key: string]: [string, ValidationErrors[]] } {
    return {
      [ Constants.PREDICTION_COLUMN_NAME_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.ACTION_COLUMN_NAMES_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.CSV_LOCATION_FORM ]: [ '', [ Validators.required ] ],
      [ Constants.NUMBER_OF_VALUES_FORM ]: [ '', [ Validators.required, Validators.min(1) ] ]
    };
  }
}
