import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../shared/shared.module';
import { ConfigurationComponent } from './input/configuration.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      { path: '', component: ConfigurationComponent }
    ]),
    SharedModule
  ],
  declarations: [
    ConfigurationComponent
  ]
})
export class ConfigurationModule { }
