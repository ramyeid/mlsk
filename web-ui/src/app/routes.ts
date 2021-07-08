import { Routes } from '@angular/router';

import { AppComponent } from './app.component';

export const appRoutes: Routes = [
  { path: '', component: AppComponent },
  { path: 'home',
    loadChildren: () => import(`./home/home.module`).then(m => m.HomeModule) },
  { path: 'time-series-analysis',
    loadChildren: () => import(`./time-series-analysis/time-series-analysis.module`).then(m => m.TimeSeriesAnalysisModule) }
];
