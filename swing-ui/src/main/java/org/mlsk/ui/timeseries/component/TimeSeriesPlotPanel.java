package org.mlsk.ui.timeseries.component;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mlsk.api.service.timeseries.model.TimeSeriesModel;

import javax.swing.*;

import static java.lang.String.format;
import static org.mlsk.ui.component.popup.ErrorPopup.tryPopup;
import static org.mlsk.ui.timeseries.helper.TimeSeriesConnector.linkComputedToInitialTimeSeries;
import static org.mlsk.ui.timeseries.mapper.TimeSeriesMapper.toTimeSeries;

public class TimeSeriesPlotPanel extends JPanel {

  public TimeSeriesPlotPanel(TimeSeriesModel initialTimeSeries,
                             TimeSeriesModel computedTimeSeries,
                             String computedTitle) {
    TimeSeries initialValues = tryPopup(() -> toTimeSeries(initialTimeSeries, "initial"), "Mapping Initial Time Series");
    TimeSeries computedValues = tryPopup(() -> toTimeSeries(computedTimeSeries, computedTitle), "Mapping Computed Time Series");
    linkComputedToInitialTimeSeries(initialValues, computedValues);

    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
    timeSeriesCollection.addSeries(initialValues);
    timeSeriesCollection.addSeries(computedValues);

    JFreeChart timeSeriesChart = ChartFactory.createTimeSeriesChart(
        format("time-series-%s", computedTitle),
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
}