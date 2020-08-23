package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.components.utils.TriFunction;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesFrame extends JFrame {

    private final TimeSeriesConfigurationPanel timeSeriesConfigurationPanel;
    private final TimeSeriesApplierPanel timeSeriesApplierPanel;
    private final TimeSeriesPlotPanel timeSeriesPlotPanel;

    public TimeSeriesFrame() {
        this.timeSeriesConfigurationPanel = new TimeSeriesConfigurationPanel();
        this.timeSeriesPlotPanel = new TimeSeriesPlotPanel();
        Supplier<TimeSeriesAnalysisRequest> buildTimeSeriesRequest = timeSeriesConfigurationPanel::buildTimeSeriesRequest;
        TriFunction<TimeSeries, TimeSeries, String> onResults = (initial, computed, title) -> {
            timeSeriesPlotPanel.setTimeSeries(initial, computed, title);
            this.repaint();
        };
        this.timeSeriesApplierPanel = new TimeSeriesApplierPanel(buildTimeSeriesRequest, onResults);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setLayout(new GridBagLayout());

        this.getContentPane().add(timeSeriesConfigurationPanel, buildGridBagConstraints(0, 0));
        this.getContentPane().add(timeSeriesPlotPanel, buildGridBagConstraints(0, 1));
        this.getContentPane().add(timeSeriesApplierPanel, buildGridBagConstraints(0, 2));
    }
}
