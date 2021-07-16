import { ChartOptions } from './chart-options';

describe('ChartOptions', () => {

  it('should build default chart options', () => {

    const actualChartOptions = ChartOptions.buildDefaultChartOptions(Helper.X_AXIS_LABEL, Helper.Y_AXIS_LABEL);

    const expectedChartOptions = Helper.buildExpectedChartOptions();
    expect(actualChartOptions).toEqual(expectedChartOptions);
  });

});

class Helper {

  static readonly X_AXIS_LABEL: string = 'xAxisLabel';
  static readonly Y_AXIS_LABEL: string = 'yAxisLabel';

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
      xAxisLabel: Helper.X_AXIS_LABEL,
      yAxisLabel: Helper.Y_AXIS_LABEL,
      autoScale: true,
      timeline: true
    };
  }
}
