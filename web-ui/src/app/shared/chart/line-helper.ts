export type ChartLine = { name: string, series: ChartCoordinate[] } ;
export type ChartLines = ChartLine[];
export type ChartCoordinate = { name: number | Date, value: number };

export class LineHelper {

  static connectLines(source: ChartLine, target: ChartLine): ChartLine {
    const firstTargetCoordinate = target.series[0];
    const coordinateIndexInSource = source.series.findIndex(serie => serie.name.valueOf() === firstTargetCoordinate.name.valueOf());
    let lastSourceCoordinate: ChartCoordinate;

    if (coordinateIndexInSource === -1) {
      lastSourceCoordinate = source.series[source.series.length - 1];
    } else {
      lastSourceCoordinate = source.series[coordinateIndexInSource - 1];
    }

    return { name: target.name, series: [ lastSourceCoordinate, ...target.series ] };
  }

  static buildCoordinate(x: number | Date, y: number): ChartCoordinate {
    return { name: x, value: y };
  }

  static buildLine(nameIn: string, points: ChartCoordinate[]): ChartLine {
    return { name: nameIn, series: points };
  }
}
