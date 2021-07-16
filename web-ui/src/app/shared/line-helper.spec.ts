import { ChartCoordinate, ChartLine, LineHelper } from './line-helper';

describe('LineHelper', () => {

  it('should connect target to end coordinate of source if first coordinate of target is not in source with numbers', () => {
    const sourcePoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(1, 300), LineHelper.buildCoordinate(2, 400), LineHelper.buildCoordinate(3, 500) ];
    const sourceLine: ChartLine = LineHelper.buildLine('source', sourcePoints);
    const targetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(4, 600), LineHelper.buildCoordinate(5, 700) ];
    const targetLine: ChartLine = LineHelper.buildLine('target', targetPoints);

    const actualTargetLine: ChartLine = LineHelper.connectLines(sourceLine, targetLine);

    const expectedTargetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(3, 500), LineHelper.buildCoordinate(4, 600), LineHelper.buildCoordinate(5, 700) ];
    const expectedTargetLine: ChartLine = LineHelper.buildLine('target', expectedTargetPoints);
    expect(sourceLine).toEqual(LineHelper.buildLine('source', sourcePoints));
    expect(targetLine).toEqual(LineHelper.buildLine('target', targetPoints));
    expect(actualTargetLine).toEqual(expectedTargetLine);
  });

  it('should connect target to coordinate of source if first coordinate of target is in source with numbers', () => {
    const sourcePoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(1, 300), LineHelper.buildCoordinate(2, 400), LineHelper.buildCoordinate(3, 500) ];
    const sourceLine: ChartLine = LineHelper.buildLine('source', sourcePoints);
    const targetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(2, 450), LineHelper.buildCoordinate(3, 550) ];
    const targetLine: ChartLine = LineHelper.buildLine('target', targetPoints);

    const actualTargetLine: ChartLine = LineHelper.connectLines(sourceLine, targetLine);

    const expectedTargetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(1, 300), LineHelper.buildCoordinate(2, 450), LineHelper.buildCoordinate(3, 550) ];
    const expectedTargetLine: ChartLine = LineHelper.buildLine('target', expectedTargetPoints);
    expect(sourceLine).toEqual(LineHelper.buildLine('source', sourcePoints));
    expect(targetLine).toEqual(LineHelper.buildLine('target', targetPoints));
    expect(actualTargetLine).toEqual(expectedTargetLine);
  });

  it('should connect target to end coordinate of source if first coordinate of target is not in source with dates', () => {
    const sourcePoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1990'), 300), LineHelper.buildCoordinate(new Date('1991'), 400), LineHelper.buildCoordinate(new Date('1992'), 500) ];
    const sourceLine: ChartLine = LineHelper.buildLine('source', sourcePoints);
    const targetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1993'), 600), LineHelper.buildCoordinate(new Date('1994'), 700) ];
    const targetLine: ChartLine = LineHelper.buildLine('target', targetPoints);

    const actualTargetLine: ChartLine = LineHelper.connectLines(sourceLine, targetLine);

    const expectedTargetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1992'), 500), LineHelper.buildCoordinate(new Date('1993'), 600), LineHelper.buildCoordinate(new Date('1994'), 700) ];
    const expectedTargetLine: ChartLine = LineHelper.buildLine('target', expectedTargetPoints);
    expect(sourceLine).toEqual(LineHelper.buildLine('source', sourcePoints));
    expect(targetLine).toEqual(LineHelper.buildLine('target', targetPoints));
    expect(actualTargetLine).toEqual(expectedTargetLine);
  });

  it('should connect target to coordinate of source if first coordinate of target is in source with dates', () => {
    const sourcePoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1990'), 300), LineHelper.buildCoordinate(new Date('1991'), 400), LineHelper.buildCoordinate(new Date('1992'), 500) ];
    const sourceLine: ChartLine = LineHelper.buildLine('source', sourcePoints);
    const targetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1992'), 550), LineHelper.buildCoordinate(new Date('1993'), 600) ];
    const targetLine: ChartLine = LineHelper.buildLine('target', targetPoints);

    const actualTargetLine: ChartLine = LineHelper.connectLines(sourceLine, targetLine);

    const expectedTargetPoints: ChartCoordinate[] = [ LineHelper.buildCoordinate(new Date('1991'), 400), LineHelper.buildCoordinate(new Date('1992'), 550), LineHelper.buildCoordinate(new Date('1993'), 600) ];
    const expectedTargetLine: ChartLine = LineHelper.buildLine('target', expectedTargetPoints);
    expect(sourceLine).toEqual(LineHelper.buildLine('source', sourcePoints));
    expect(targetLine).toEqual(LineHelper.buildLine('target', targetPoints));
    expect(actualTargetLine).toEqual(expectedTargetLine);
  });

});
