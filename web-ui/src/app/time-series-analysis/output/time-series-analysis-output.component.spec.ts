import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LineChartComponent, NgxChartsModule } from '@swimlane/ngx-charts';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

import { TimeSeries } from '../model/time-series';
import { TimeSeriesRow } from '../model/time-series-row';
import { TimeSeriesType } from '../model/time-series-type';
import { TimeSeriesAnalysisOutputComponent } from './time-series-analysis-output.component';
import { ChartCoordinate, ChartLine, ChartLines, LineHelper } from 'src/app/shared/line-helper';
import { ChartOptions } from 'src/app/shared/chart-options';

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

    it('should render page without chart on load', () => {

      fixture.detectChanges();

      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(ngxLineChart).toBeNull();
    });

    it('should render page without chart when only request is received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();

      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);
      fixture.detectChanges();

      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(ngxLineChart).toBeNull();
    });

    it('should render page with chart when request and result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();

      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);
      component.onTimeSeriesResult([timeSeriesResult, TimeSeriesType.RESULT]);
      fixture.detectChanges();

      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(ngxLineChart).not.toBeNull();
      Helper.expectCorrectLineChartComponent(ngxLineChart.componentInstance);
    });

    it('should render page without chart when request and result received and new request', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();
      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);
      component.onTimeSeriesResult([timeSeriesResult, TimeSeriesType.RESULT]);
      fixture.detectChanges();

      component.onNewRequest();
      fixture.detectChanges();

      const ngxLineChart: DebugElement = fixture.debugElement.query(By.css('ngx-charts-line-chart'));
      expect(ngxLineChart).toBeNull();
    });

  });


  describe('TimeSeriesResult', () => {

    it('should not set display to true when only request is received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();

      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);

      expect(component.chartOptions).toEqual(Helper.buildExpectedChartOptions());
      expect(component.chartLines).toEqual([ Helper.buildExpectedRequestChartLine() ]);
      expect(component.shouldDisplay).toBeFalse();
    });

    it ('should build chart lines when request and result are received', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();

      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);
      component.onTimeSeriesResult([timeSeriesResult, TimeSeriesType.RESULT]);

      expect(component.chartOptions).toEqual(Helper.buildExpectedChartOptions());
      expect(component.chartLines).toEqual(Helper.buildExpectedChartLines());
      expect(component.shouldDisplay).toBeTrue();
    });

  });


  describe('NewRequest', () => {

    it('should reset chart options, chart lines and display on new request', () => {
      const timeSeriesRequest: TimeSeries = Helper.buildTimeSeriesRequest();
      const timeSeriesResult: TimeSeries = Helper.buildTimeSeriesResult();
      component.onTimeSeriesResult([timeSeriesRequest, TimeSeriesType.REQUEST]);
      component.onTimeSeriesResult([timeSeriesResult, TimeSeriesType.RESULT]);

      component.onNewRequest();

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

}