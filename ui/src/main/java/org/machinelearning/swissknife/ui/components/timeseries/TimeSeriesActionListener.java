package org.machinelearning.swissknife.ui.components.timeseries;

import org.machinelearning.swissknife.model.timeseries.TimeSeries;
import org.machinelearning.swissknife.model.timeseries.TimeSeriesAnalysisRequest;
import org.machinelearning.swissknife.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.machinelearning.swissknife.ui.components.utils.TriFunction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import static org.machinelearning.swissknife.ui.components.utils.ErrorPopup.tryPopup;

public class TimeSeriesActionListener implements ActionListener {

    public static final String PREDICT_COMMAND = "Predict";
    public static final String FORECAST_COMMAND = "Forecast";

    private final TimeSeriesInputPanel timeSeriesInputPanel;
    private final TriFunction<TimeSeries, TimeSeries, String> onResults;
    private final TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;

    public TimeSeriesActionListener(TimeSeriesInputPanel timeSeriesInputPanel,
                                    TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient,
                                    TriFunction<TimeSeries, TimeSeries, String> onResults) {

        this.timeSeriesInputPanel = timeSeriesInputPanel;
        this.onResults = onResults;
        this.timeSeriesAnalysisServiceClient = timeSeriesAnalysisServiceClient;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = timeSeriesInputPanel.buildTimeSeriesRequest();
        String actionCommand = actionEvent.getActionCommand();
        Callable<TimeSeries> callable = getCallback(actionCommand, timeSeriesAnalysisRequest);

        TimeSeries computedTimeSeries = tryPopup(callable, String.format("Error while launching %s", actionCommand));

        onResults.apply(timeSeriesAnalysisRequest.getTimeSeries(), computedTimeSeries, actionCommand.toLowerCase());
    }

    private Callable<TimeSeries> getCallback(String action, TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
        Callable<TimeSeries> callable;
        if (action.equals(PREDICT_COMMAND)) {
            callable = () -> timeSeriesAnalysisServiceClient.predict(timeSeriesAnalysisRequest);
        } else {
            callable = () -> timeSeriesAnalysisServiceClient.forecast(timeSeriesAnalysisRequest);
        }
        return callable;
    }
}
