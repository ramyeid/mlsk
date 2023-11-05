import { Component, ViewChild } from '@angular/core';

import { InputEmitType } from 'src/app/shared/model/input-emit-type';
import { ClassifierResponse } from '../../model/classifier-response';
import { DecisionTreeOutputComponent } from '../output/decision-tree-output.component';

@Component({
  selector: 'mlsk-decision-tree',
  templateUrl: './decision-tree.component.html'
})
export class DecisionTreeComponent {

  @ViewChild(DecisionTreeOutputComponent) outputComponent: DecisionTreeOutputComponent;

  onResult(resultAndType: [ClassifierResponse | number, InputEmitType]): void {
    this.outputComponent.onResult(resultAndType);
  }

  onNewRequest(): void {
    this.outputComponent.onNewRequest();
  }
}
