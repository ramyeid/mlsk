package org.mlsk.ui.timeseries.helper;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public final class TimeSeriesConnector {

  private TimeSeriesConnector() {
  }

  public static void linkComputedToInitialTimeSeries(TimeSeries initial, TimeSeries computed) {
    // In order not to have a blank between the last element in initial and the first element in computed.
    List<RegularTimePeriod> timePeriodsNotInComputed = newArrayList(computed.getTimePeriodsUniqueToOtherSeries(initial));
    if (!timePeriodsNotInComputed.isEmpty()) {
      RegularTimePeriod lastTimePeriodNotInComputed = timePeriodsNotInComputed.get(timePeriodsNotInComputed.size() - 1);
      TimeSeriesDataItem lastDataItemNotInComputed = initial.getDataItem(lastTimePeriodNotInComputed);
      computed.add(lastDataItemNotInComputed);
    }
  }
}
