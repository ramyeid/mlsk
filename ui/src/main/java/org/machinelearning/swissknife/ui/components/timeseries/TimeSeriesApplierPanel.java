package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.machinelearning.swissknife.ui.components.utils.TriFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.ui.components.utils.ComponentBuilder.newJButton;
import static org.machinelearning.swissknife.ui.components.utils.ErrorPopup.tryPopup;
import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesApplierPanel extends JPanel implements ActionListener {

    private static final String PREDICT_COMMAND = "Predict";
    private static final String FORECAST_COMMAND = "Forecast";

    private final Supplier<TimeSeriesAnalysisRequest> getTimeSeriesAnalysisRequest;
    private final TriFunction<TimeSeries, TimeSeries, String> onResults;
    private final TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;

    public TimeSeriesApplierPanel(TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient,
                                  Supplier<TimeSeriesAnalysisRequest> getTimeSeriesAnalysisRequest,
                                  TriFunction<TimeSeries, TimeSeries, String> onResults) {

        this.getTimeSeriesAnalysisRequest = getTimeSeriesAnalysisRequest;
        this.onResults = onResults;
        this.timeSeriesAnalysisServiceClient = timeSeriesAnalysisServiceClient;

        JButton predictButton = newJButton(PREDICT_COMMAND, this);
        JButton forecastButton = newJButton(FORECAST_COMMAND, this);

        this.setLayout(new GridBagLayout());
        this.add(predictButton, buildGridBagConstraints(0, 0));
        this.add(forecastButton, buildGridBagConstraints(1, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = getTimeSeriesAnalysisRequest.get();
        Callable<TimeSeries> callable;
        if (e.getActionCommand().equals(PREDICT_COMMAND)) {
            callable = () -> timeSeriesAnalysisServiceClient.predict(timeSeriesAnalysisRequest);
        } else {
            callable = () -> timeSeriesAnalysisServiceClient.forecast(timeSeriesAnalysisRequest);
        }
        TimeSeries computedTimeSeries = tryPopup(callable, String.format("Error while launching %s", e.getActionCommand()));
        onResults.apply(timeSeriesAnalysisRequest.getTimeSeries(), computedTimeSeries, e.getActionCommand().toLowerCase());
    }
}
