package org.mlsk.ui.timeseries.helper;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mlsk.ui.timeseries.helper.TimeSeriesConnector.linkComputedToInitialTimeSeries;

public class TimeSeriesConnectorTest {

  @Test
  public void should_link_the_time_series() {
    TimeSeries initial = buildInitialTimeSeries();
    TimeSeries computed = buildComputedTimeSeries();

    linkComputedToInitialTimeSeries(initial, computed);

    assertEquals(buildInitialTimeSeries(), initial);
    assertEquals(buildExpectedComputedTimeSeries(), computed);
  }

  @Test
  public void should_do_nothing_if_empty_time_series() {
    TimeSeries initial = new TimeSeries("emptyInitial");
    TimeSeries computed = new TimeSeries("emptyComputed");

    linkComputedToInitialTimeSeries(initial, computed);

    assertEquals(new TimeSeries("emptyInitial"), initial);
    assertEquals(new TimeSeries("emptyComputed"), computed);
  }

  @Test
  public void should_do_nothing_if_all_initial_is_in_computed() {
    TimeSeries initial = buildInitialTimeSeries();
    TimeSeries computed = buildComputedTimeSeries2();

    linkComputedToInitialTimeSeries(initial, computed);

    assertEquals(buildInitialTimeSeries(), initial);
    assertEquals(buildComputedTimeSeries2(), computed);
  }

  private static TimeSeries buildInitialTimeSeries() {
    TimeSeries timeSeries = new TimeSeries("initial");

    timeSeries.add(new Day(1, 1, 2020), 100);
    timeSeries.add(new Day(2, 1, 2020), 200);
    timeSeries.add(new Day(3, 1, 2020), 300);

    return timeSeries;
  }

  private static TimeSeries buildComputedTimeSeries() {
    TimeSeries timeSeries = new TimeSeries("computed");

    timeSeries.add(new Day(4, 1, 2020), 400);
    timeSeries.add(new Day(5, 1, 2020), 500);
    timeSeries.add(new Day(6, 1, 2020), 600);

    return timeSeries;
  }

  private static TimeSeries buildComputedTimeSeries2() {
    TimeSeries timeSeries = new TimeSeries("initial");

    timeSeries.add(new Day(1, 1, 2020), 100);
    timeSeries.add(new Day(2, 1, 2020), 200);
    timeSeries.add(new Day(3, 1, 2020), 300);
    timeSeries.add(new Day(4, 1, 2020), 400);
    timeSeries.add(new Day(5, 1, 2020), 500);
    timeSeries.add(new Day(6, 1, 2020), 600);

    return timeSeries;
  }

  private static TimeSeries buildExpectedComputedTimeSeries() {
    TimeSeries timeSeries = new TimeSeries("computed");

    timeSeries.add(new Day(3, 1, 2020), 300);
    timeSeries.add(new Day(4, 1, 2020), 400);
    timeSeries.add(new Day(5, 1, 2020), 500);
    timeSeries.add(new Day(6, 1, 2020), 600);

    return timeSeries;
  }

}