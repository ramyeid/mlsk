import { Component } from '@angular/core';
import { ClassifierResponse } from '../../model/classifier-response';
import { InputEmitType } from 'src/app/shared/model/input-emit-type';

@Component({
  selector: 'mlsk-decision-tree-output',
  templateUrl: './decision-tree-output.component.html'
})
export class DecisionTreeOutputComponent {

  temporaryResultUntilCorrectOutputIsImplemented: string;

  onNewRequest(): void {
    this.temporaryResultUntilCorrectOutputIsImplemented = 'onNewRequest called';
  }

  onResult(resultAndType: [ClassifierResponse | number, InputEmitType]): void {
    console.log(resultAndType[0]);
    this.temporaryResultUntilCorrectOutputIsImplemented = 'onResult called ' + resultAndType[0];
  }

}
