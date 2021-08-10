package org.mlsk.ui.components.timeseries;

import org.mlsk.service.model.timeseries.TimeSeries;
import org.mlsk.service.model.timeseries.TimeSeriesAnalysisRequest;
import org.mlsk.ui.client.timeseries.TimeSeriesAnalysisServiceClient;
import org.mlsk.ui.components.utils.TriFunction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.concurrent.Callable;

import static org.mlsk.ui.components.utils.ErrorPopup.tryPopup;

public class TimeSeriesActionListener implements ActionListener {

  public static final String PREDICT_COMMAND = "Predict";
  public static final String FORECAST_COMMAND = "Forecast";
  public static final String FORECAST_VS_ACTUAL_COMMAND = "Forecast vs Actual";
  public static final String FORECAST_ACCURACY_COMMAND = "Forecast Accuracy";

  private final TimeSeriesInputPanel timeSeriesInputPanel;
  private final TriFunction<TimeSeries, Object, String> onResults;
  private final TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient;

  public TimeSeriesActionListener(TimeSeriesInputPanel timeSeriesInputPanel,
                                  TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient,
                                  TriFunction<TimeSeries, Object, String> onResults) {

    this.timeSeriesInputPanel = timeSeriesInputPanel;
    this.onResults = onResults;
    this.timeSeriesAnalysisServiceClient = timeSeriesAnalysisServiceClient;
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    TimeSeriesAnalysisRequest timeSeriesAnalysisRequest = timeSeriesInputPanel.buildTimeSeriesRequest();
    String actionCommand = actionEvent.getActionCommand();
    Callable<Object> callable = getCallback(actionCommand, timeSeriesAnalysisRequest);

    Object result = tryPopup(callable, String.format("Error while launching %s", actionCommand));

    onResults.apply(timeSeriesAnalysisRequest.getTimeSeries(), result, getTimeSeriesTitle(actionCommand));
  }

  private String getTimeSeriesTitle(String actionCommand) {
    switch (actionCommand) {
      case PREDICT_COMMAND:
      case FORECAST_ACCURACY_COMMAND:
        return actionCommand.toLowerCase(Locale.ROOT);
      case FORECAST_COMMAND:
      case FORECAST_VS_ACTUAL_COMMAND:
        return FORECAST_COMMAND.toLowerCase(Locale.ROOT);
      default:
        return null;
    }
  }

  private Callable<Object> getCallback(String actionCommand, TimeSeriesAnalysisRequest timeSeriesAnalysisRequest) {
    switch (actionCommand) {
      case PREDICT_COMMAND:
        return () -> timeSeriesAnalysisServiceClient.predict(timeSeriesAnalysisRequest);
      case FORECAST_COMMAND:
        return () -> timeSeriesAnalysisServiceClient.forecast(timeSeriesAnalysisRequest);
      case FORECAST_VS_ACTUAL_COMMAND:
        return () -> timeSeriesAnalysisServiceClient.forecastVsActual(timeSeriesAnalysisRequest);
      case FORECAST_ACCURACY_COMMAND:
        return () -> timeSeriesAnalysisServiceClient.computeForecastAccuracy(timeSeriesAnalysisRequest);
      default:
        return () -> null;
    }
  }
}
