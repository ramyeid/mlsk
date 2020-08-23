package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.TimeSeriesAnalysis;
import org.machinelearning.swissknife.model.ServiceInformation;
import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.machinelearning.swissknife.ui.components.utils.TriFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.machinelearning.swissknife.ui.Application.SERVICE_INFORMATION;
import static org.machinelearning.swissknife.ui.components.utils.ErrorPopup.tryPopup;
import static org.machinelearning.swissknife.ui.components.utils.GridBagUtils.buildGridBagConstraints;

public class TimeSeriesApplierPanel extends JPanel implements ActionListener {

    private static final String PREDICT_COMMAND = "PREDICT";
    private static final String FORECAST_COMMAND = "FORECAST";

    private final Supplier<TimeSeriesAnalysisRequest> getTimeSeriesAnalysisRequest;
    private final TriFunction<TimeSeries, TimeSeries, String> onResults;

    public TimeSeriesApplierPanel(Supplier<TimeSeriesAnalysisRequest> getTimeSeriesAnalysisRequest,
                                  TriFunction<TimeSeries, TimeSeries, String> onResults) {

        this.getTimeSeriesAnalysisRequest = getTimeSeriesAnalysisRequest;
        this.onResults = onResults;

        JButton predictButton = new JButton("Predict");
        JButton forecastButton = new JButton("Forecast");

        predictButton.setActionCommand(PREDICT_COMMAND);
        forecastButton.setActionCommand(FORECAST_COMMAND);
        predictButton.addActionListener(this);
        forecastButton.addActionListener(this);

        this.setLayout(new GridBagLayout());
        this.add(predictButton, buildGridBagConstraints(0, 0));
        this.add(forecastButton, buildGridBagConstraints(1, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = getTimeSeriesAnalysisRequest.get();
        TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient = new TimeSeriesAnalysisServiceClient(SERVICE_INFORMATION);
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
