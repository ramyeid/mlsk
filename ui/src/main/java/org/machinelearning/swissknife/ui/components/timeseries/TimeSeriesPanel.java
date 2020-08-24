package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.machinelearning.swissknife.ui.components.utils.TriFunction;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesPanel extends JPanel {

    private final JPanel configuration;
    private final TimeSeriesConfigurationPanel timeSeriesConfigurationPanel;
    private final TimeSeriesApplierPanel timeSeriesApplierPanel;
    private final JPanel output;
    private TimeSeriesPlotPanel timeSeriesPlotPanel;

    public TimeSeriesPanel(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
        this.timeSeriesConfigurationPanel = new TimeSeriesConfigurationPanel();
        Supplier<TimeSeriesAnalysisRequest> buildTimeSeriesRequest = timeSeriesConfigurationPanel::buildTimeSeriesRequest;
        TriFunction<TimeSeries, TimeSeries, String> onResults = this::addTimeSeriesPlot;
        this.timeSeriesApplierPanel = new TimeSeriesApplierPanel(timeSeriesAnalysisServiceClient, buildTimeSeriesRequest, onResults);

        this.setLayout(new BorderLayout());

        configuration = new JPanel();
        configuration.setLayout(new GridBagLayout());
        configuration.add(timeSeriesConfigurationPanel, buildGridBagConstraints(0, 0));
        configuration.add(timeSeriesApplierPanel, buildGridBagConstraints(0, 1));
        output = new JPanel();

        this.add(configuration, BorderLayout.NORTH);
        this.add(output, BorderLayout.CENTER);
    }

    private void addTimeSeriesPlot(TimeSeries initial, TimeSeries computed, String title) {
        this.output.removeAll();
        this.timeSeriesPlotPanel = new TimeSeriesPlotPanel(initial, computed, title);
        this.output.add(timeSeriesPlotPanel);
        SwingUtilities.updateComponentTreeUI(this);
    }
}