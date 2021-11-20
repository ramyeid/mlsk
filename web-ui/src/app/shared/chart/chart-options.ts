export class ChartOptions {

  view: [number, number];
  scheme: string;
  showXAxis: boolean;
  showYAxis: boolean;
  gradient: boolean;
  showLegend: boolean;
  showXAxisLabel: boolean;
  showYAxisLabel: boolean;
  xAxisLabel: string;
  yAxisLabel: string;
  autoScale: boolean;
  timeline: boolean;

  static buildDefaultChartOptions(xAxisLabelIn: string, yAxisLabelIn: string): ChartOptions {
    return {
      view: [700, 400],
      scheme: 'vivid',
      showXAxis: true,
      showYAxis: true,
      gradient: true,
      showLegend: true,
      showXAxisLabel: true,
      showYAxisLabel: true,
      xAxisLabel: xAxisLabelIn,
      yAxisLabel: yAxisLabelIn,
      autoScale: true,
      timeline: true
    };
  }
}
