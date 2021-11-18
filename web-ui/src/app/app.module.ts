import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { ConfigurationModule } from './configuration/configuration.module';
import { TimeSeriesAnalysisModule } from './time-series-analysis/time-series-analysis.module';
import { DecisionTreeModule } from './classifier/decision-tree/decision-tree.module';
import { HomeModule } from './home/home.module';
import { SharedModule } from './shared/shared.module';
import { appRoutes } from './routes';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    SharedModule,
    HomeModule,
    ConfigurationModule,
    TimeSeriesAnalysisModule,
    DecisionTreeModule,
    RouterModule.forRoot(appRoutes)
  ],
  declarations: [
    AppComponent
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
