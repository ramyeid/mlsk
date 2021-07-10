package org.mlsk.ui.components.timeseries;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import org.mlsk.service.model.timeseries.TimeSeriesRow;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.mlsk.ui.components.utils.ErrorPopup.tryPopup;

public class TimeSeriesPlotPanel extends JPanel {

  public TimeSeriesPlotPanel(org.mlsk.service.model.timeseries.TimeSeries initialTimeSeries,
                             org.mlsk.service.model.timeseries.TimeSeries computedTimeSeries,
                             String action) {

    TimeSeries initialValues = toTimeSeries(initialTimeSeries, "initial");
    TimeSeries computedValues = toTimeSeries(computedTimeSeries, action);
    linkComputedToInitialTimeSeries(initialValues, computedValues);

    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
    timeSeriesCollection.addSeries(initialValues);
    timeSeriesCollection.addSeries(computedValues);

    JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
        String.format("time-series-%s", action),
        initialTimeSeries.getDateColumnName(),
        initialTimeSeries.getValueColumnName(),
        timeSeriesCollection,
        true,
        true,
        false);

    ChartPanel chartPanel = new ChartPanel(timeSeriesChart);
    chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));

    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setHorizontalAxisTrace(true);
    chartPanel.setVerticalAxisTrace(true);

    this.add(chartPanel, 0);
  }

  private void linkComputedToInitialTimeSeries(TimeSeries initial, TimeSeries computed) {
    // In order not to have a blank between the last element in initial and the first element in computed.
    List<RegularTimePeriod> timePeriodsNotInComputed = new ArrayList<>(computed.getTimePeriodsUniqueToOtherSeries(initial));
    RegularTimePeriod lastTimePeriodNotInComputed = timePeriodsNotInComputed.get(timePeriodsNotInComputed.size() - 1);
    TimeSeriesDataItem lastDataItemNotInComputed = initial.getDataItem(lastTimePeriodNotInComputed);
    computed.add(lastDataItemNotInComputed);
  }

  private static TimeSeries toTimeSeries(org.mlsk.service.model.timeseries.TimeSeries initialTimeSeries,
                                         String timeSeriesTitle) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat(initialTimeSeries.getDateFormat());

    TimeSeries series = new TimeSeries(timeSeriesTitle);
    for (TimeSeriesRow row : initialTimeSeries.getRows()) {
      Day period = tryPopup(() -> new Day(dateFormatter.parse(row.getDate())), String.format("Could not format %s with formatter %s", row.getDate(), initialTimeSeries.getDateFormat()));
      series.add(period, row.getValue());
    }
    return series;
  }
}