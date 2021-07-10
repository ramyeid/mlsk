package org.mlsk.ui.components.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.components.utils.TriFunction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import static org.mlsk.ui.components.utils.ErrorPopup.tryPopup;

public class TimeSeriesActionListener implements ActionListener {

  public static final String PREDICT_COMMAND = "Predict";
  public static final String FORECAST_COMMAND = "Forecast";
  public static final String FORECAST_VS_ACTUAL = "Forecast vs Actual";

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

    onResults.apply(timeSeriesAnalysisRequest.getTimeSeries(), computedTimeSeries, getTimeSeriesTitle(actionCommand));
  }

  private String getTimeSeriesTitle(String actionCommand) {
    if (actionCommand.equals(PREDICT_COMMAND)) {
      return actionCommand.toLowerCase();
    } else {
      return FORECAST_COMMAND.toLowerCase();
    }
  }

  private Callable<TimeSeries> getCallback(String actionCommand, TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    Callable<TimeSeries> callable;
    if (actionCommand.equals(PREDICT_COMMAND)) {
      callable = () -> timeSeriesAnalysisServiceClient.predict(timeSeriesAnalysisRequest);
    } else if (actionCommand.equals(FORECAST_COMMAND)) {
      callable = () -> timeSeriesAnalysisServiceClient.forecast(timeSeriesAnalysisRequest);
    } else {
      callable = () -> timeSeriesAnalysisServiceClient.forecastVsActual(timeSeriesAnalysisRequest);
    }
    return callable;
  }
}
