package org.machinelearning.swissknife.ui.components.timeseries;

import com.google.common.annotations.VisibleForTesting;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;

import javax.swing.*;
import java.awt.*;

import static org.machinelearning.swissknife.ui.ServiceConfiguration.getServiceInformation;

public class TimeSeriesPanel extends JPanel {

    private final JPanel plotPanel;

    public TimeSeriesPanel() {
        this(new TimeSeriesAnalysisServiceClient(getServiceInformation()));
    }

    @VisibleForTesting
    public TimeSeriesPanel(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient) {
        this.plotPanel = new JPanel();
        this.setLayout(new BorderLayout());

        TimeSeriesInputPanel timeSeriesInputPanel = new TimeSeriesInputPanel();
        timeSeriesInputPanel.setActionListener(new TimeSeriesActionListener(timeSeriesInputPanel, timeSeriesAnalysisServiceClient, this::addTimeSeriesPlot));
        this.add(timeSeriesInputPanel, BorderLayout.NORTH);

        this.add(plotPanel, BorderLayout.CENTER);
    }

    private void addTimeSeriesPlot(TimeSeries initial, TimeSeries computed, String title) {
        this.plotPanel.removeAll();
        this.plotPanel.add(new TimeSeriesPlotPanel(initial, computed, title));
        SwingUtilities.updateComponentTreeUI(this);
    }
}