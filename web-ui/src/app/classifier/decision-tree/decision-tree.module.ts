import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from './../../shared/shared.module';
import { DecisionTreeComponent } from './main/decision-tree.component';
import { DecisionTreeInputComponent } from './input/decision-tree-input.component';
import { DecisionTreeOutputComponent } from './output/decision-tree-output.component';

@NgModule({
  imports: [
    SharedModule,
    RouterModule.forChild([
      { path: '', component: DecisionTreeComponent }
    ]),
  ],
  declarations: [
    DecisionTreeComponent,
    DecisionTreeInputComponent,
    DecisionTreeOutputComponent
  ]
})
export class DecisionTreeModule { }
