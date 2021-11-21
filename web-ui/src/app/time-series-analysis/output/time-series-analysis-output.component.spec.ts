import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LineChartComponent, NgxChartsModule } from '@swimlane/ngx-charts';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { TimeSeriesEmittedType } from '../model/time-series-emitted-type';
import { TimeSeriesAnalysisOutputComponent } from './time-series-analysis-output.component';
import { ChartCoordinate, ChartLine, ChartLines, LineHelper } from 'src/app/shared/chart/line-helper';
import { ChartOptions } from 'src/app/shared/chart/chart-options';
import { Constants } from '../utils/constants';

describe('TimeSeriesAnalysisOutputComponent', () => {

  let fixture: ComponentFixture<TimeSeriesAnalysisOutputComponent>;
  let component: TimeSeriesAnalysisOutputComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ NoopAnimationsModule, NgxChartsModule ],
      declarations: [ TimeSeriesAnalysisOutputComponent, LineChartComponent ],
    });

    fixture = TestBed.createComponent(TimeSeriesAnalysisOutputComponent);
    component = fixture.componentInstance;
  });


  describe('Component Rendering', () => {

    it('should render page without chart and accuracy on load', () => {

      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).toBeNull();
      expect(ngxLineChart).toBeNull();
    });

    it('should render page without chart and accuracy when only request is received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).toBeNull();
      expect(ngxLineChart).toBeNull();
    });

    it('should render page with chart when request and time series result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([timeSeriesResult, TimeSeriesEmittedType.RESULT]);
      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).toBeNull();
      expect(ngxLineChart).not.toBeNull();
      Helper.expectCorrectLineChartComponent(ngxLineChart.componentInstance);
    });

    it('should render page with accuracy when request and forecast accuracy result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const forecastAccuracyResult: number = Helper.buildForecastAccuracyResult();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([forecastAccuracyResult, TimeSeriesEmittedType.RESULT]);
      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).not.toBeNull();
      expect(ngxLineChart).toBeNull();
      Helper.expectCorrectForecastMessage(accuracyResult.nativeElement);
    });

    it('should render page without chart when request and result received and new request', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();
      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([timeSeriesResult, TimeSeriesEmittedType.RESULT]);
      fixture.detectChanges();

      component.onNewRequest();
      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).toBeNull();
      expect(ngxLineChart).toBeNull();
    });

    it('should render page without accuracy when request and result received and new request', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const forecastAccuracyResult: number = Helper.buildForecastAccuracyResult();
      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([forecastAccuracyResult, TimeSeriesEmittedType.RESULT]);
      fixture.detectChanges();

      component.onNewRequest();
      fixture.detectChanges();

      const accuracyResult: DebugElement = fixture.debugElement.query(By.css(`#${Constants.ACCURACY_RESULT_P}`));
      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(accuracyResult).toBeNull();
      expect(ngxLineChart).toBeNull();
    });

  });


  describe('TimeSeriesResult & AccuracyResult', () => {

    it('should not set display to true when only request is received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);

      expect(component.forecastAccuracy).toBeUndefined();
      expect(component.chartOptions).toEqual(Helper.buildExpectedChartOptions());
      expect(component.chartLines).toEqual([ Helper.buildExpectedRequestChartLine() ]);
      expect(component.shouldDisplay).toBeFalse();
    });

    it ('should build chart lines when request and time series result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([timeSeriesResult, TimeSeriesEmittedType.RESULT]);

      expect(component.forecastAccuracy).toBeUndefined();
      expect(component.chartOptions).toEqual(Helper.buildExpectedChartOptions());
      expect(component.chartLines).toEqual(Helper.buildExpectedChartLines());
      expect(component.shouldDisplay).toBeTrue();
    });

    it('should set forecast accuracy when request and forecast accuracy result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const forecastAccuracyResult: number = Helper.buildForecastAccuracyResult();

      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([forecastAccuracyResult, TimeSeriesEmittedType.RESULT]);

      expect(component.forecastAccuracy).toBe(String(Helper.buildForecastAccuracyResult()));
      expect(component.chartOptions).toEqual(Helper.buildExpectedChartOptions());
      expect(component.chartLines).toEqual([ Helper.buildExpectedRequestChartLine() ]);
      expect(component.shouldDisplay).toBeFalse();
    });

  });


  describe('NewRequest', () => {

    it('should reset chart options, chart lines and display on new request after time series result', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();
      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([timeSeriesResult, TimeSeriesEmittedType.RESULT]);

      component.onNewRequest();

      expect(component.forecastAccuracy).toBeUndefined();
      expect(component.chartOptions).toBeUndefined();
      expect(component.chartLines).toEqual([]);
      expect(component.shouldDisplay).toBeFalse();
    });

    it('should reset chart options, chart lines and display on new request after forecast accuracy result', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const forecastAccuracyResult: number = Helper.buildForecastAccuracyResult();
      component.onResult([timeSeriesRequest, TimeSeriesEmittedType.REQUEST]);
      component.onResult([forecastAccuracyResult, TimeSeriesEmittedType.RESULT]);

      component.onNewRequest();

      expect(component.forecastAccuracy).toBeUndefined();
      expect(component.chartOptions).toBeUndefined();
      expect(component.chartLines).toEqual([]);
      expect(component.shouldDisplay).toBeFalse();
    });

  });

});

class Helper {

  private static readonly DATE_COLUMN_VALUE = 'date';
  private static readonly VALUE_COLUMN_VALUE = 'value';

  static buildTimeSeriesRequest(): TimeSeries {
    const row1: TimeSeriesRow = new TimeSeriesRow('1960', 1);
    const row2: TimeSeriesRow = new TimeSeriesRow('1961', 2);
    const row3: TimeSeriesRow = new TimeSeriesRow('1962', 3);
    return new TimeSeries([ row1, row2, row3 ], Helper.DATE_COLUMN_VALUE, Helper.VALUE_COLUMN_VALUE, 'yyyy');
  }

  static buildTimeSeriesResult(): TimeSeries {
    const row1: TimeSeriesRow = new TimeSeriesRow('1963', 4);
    const row2: TimeSeriesRow = new TimeSeriesRow('1964', 5);
    const row3: TimeSeriesRow = new TimeSeriesRow('1965', 6);
    return new TimeSeries([ row1, row2, row3 ], Helper.DATE_COLUMN_VALUE, Helper.VALUE_COLUMN_VALUE, 'yyyy');
  }

  static buildForecastAccuracyResult(): number {
    return 78.123;
  }

  static buildExpectedRequestChartLine(): ChartLine {
    const point1: ChartCoordinate = LineHelper.buildCoordinate(new Date('1960'), 1);
    const point2: ChartCoordinate = LineHelper.buildCoordinate(new Date('1961'), 2);
    const point3: ChartCoordinate = LineHelper.buildCoordinate(new Date('1962'), 3);
    return LineHelper.buildLine('Data', [ point1, point2, point3 ]);
  }

  static buildExpectedResultChartLine(): ChartLine {
    const point1: ChartCoordinate = LineHelper.buildCoordinate(new Date('1962'), 3);
    const point2: ChartCoordinate = LineHelper.buildCoordinate(new Date('1963'), 4);
    const point3: ChartCoordinate = LineHelper.buildCoordinate(new Date('1964'), 5);
    const point4: ChartCoordinate = LineHelper.buildCoordinate(new Date('1965'), 6);
    return LineHelper.buildLine('Result', [ point1, point2, point3, point4 ]);
  }

  static buildExpectedChartLines(): ChartLines {
    return [ Helper.buildExpectedRequestChartLine(), Helper.buildExpectedResultChartLine() ];
  }

  static buildExpectedChartOptions(): ChartOptions {
    return {
      view: [700, 400],
      scheme: 'vivid',
      showXAxis: true,
      showYAxis: true,
      gradient: true,
      showLegend: true,
      showXAxisLabel: true,
      showYAxisLabel: true,
      xAxisLabel: Helper.DATE_COLUMN_VALUE,
      yAxisLabel: Helper.VALUE_COLUMN_VALUE,
      autoScale: true,
      timeline: true
    };
  }

  static expectCorrectLineChartComponent(lineChartComponent: LineChartComponent): void {
    expect(lineChartComponent.view).toEqual([700, 400]);
    expect(lineChartComponent.scheme).toEqual('vivid');
    expect(lineChartComponent.results).toEqual(Helper.buildExpectedChartLines());
    expect(lineChartComponent.xAxis).toBeTrue();
    expect(lineChartComponent.yAxis).toBeTrue();
    expect(lineChartComponent.legend).toBeTrue();
    expect(lineChartComponent.showXAxisLabel).toBeTrue();
    expect(lineChartComponent.showYAxisLabel).toBeTrue();
    expect(lineChartComponent.xAxisLabel).toEqual(Helper.DATE_COLUMN_VALUE);
    expect(lineChartComponent.yAxisLabel).toEqual(Helper.VALUE_COLUMN_VALUE);
    expect(lineChartComponent.autoScale).toBeTrue();
    expect(lineChartComponent.timeline).toBeTrue();
  }

  static expectCorrectForecastMessage(nativeElement: HTMLElement): void {
    expect(nativeElement.textContent).toEqual('Forecast Accuracy: 78.123%');
  }

}
