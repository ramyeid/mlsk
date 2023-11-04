import { Component, ViewChild } from '@angular/core';

import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { ClassifierDataResponse } from '../../model/classifier-data-response';
import { DecisionTreeOutputComponent } from '../output/decision-tree-output.component';

@Component({
  selector: 'mlsk-decision-tree',
  templateUrl: './decision-tree.component.html'
})
export class DecisionTreeComponent {

  @ViewChild(DecisionTreeOutputComponent) outputComponent: DecisionTreeOutputComponent;

  onResult(resultAndType: [ClassifierDataResponse | number, InputEmitType]): void {
    this.outputComponent.onResult(resultAndType);
  }

  onNewRequest(): void {
    this.outputComponent.onNewRequest();
  }
}
