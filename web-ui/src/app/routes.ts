import { Routes } from '@angular/router';

import { AppComponent } from './app.component';

export const appRoutes: Routes = [
  { path: '',
    component: AppComponent
  },

  { path: 'home',
    loadChildren: (): Promise<unknown> => import('./home/home.module').then(m => m.HomeModule)
  },

  { path: 'time-series-analysis',
    loadChildren: (): Promise<unknown> => import('./time-series-analysis/time-series-analysis.module').then(m => m.TimeSeriesAnalysisModule)
  },

  {
    path: 'decision-tree',
    loadChildren: (): Promise<unknown> => import('./classifier/decision-tree/decision-tree.module').then(m => m.DecisionTreeModule)
  },

  { path : 'config',
    loadChildren: (): Promise<unknown> => import('./configuration/configuration.module').then(m => m.ConfigurationModule)
  }
];
