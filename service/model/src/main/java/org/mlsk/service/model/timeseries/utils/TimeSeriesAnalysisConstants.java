package org.mlsk.service.model.timeseries.utils;

public final class TimeSeriesAnalysisConstants {

  /**
   * Service & Engine#forecast URL
   */
  public static final String FORECAST_URL = "/time-series-analysis/forecast";
  /**
   * Service#forecastVsActual URL
   */
  public static final String FORECAST_VS_ACTUAL_URL = "/time-series-analysis/forecast-vs-actual";
  /**
   * Service & Engine#forecastAccuracy URL
   */
  public static final String FORECAST_ACCURACY_URL = "/time-series-analysis/forecast-accuracy";
  /**
   * Service & Engine#predict URL
   */
  public static final String PREDICT_URL = "/time-series-analysis/predict";

  public static final String TIME_SERIES_FORECAST = "time-series-forecast";
  public static final String TIME_SERIES_FORECAST_VS_ACTUAL = "time-series-forecast-vs-actual";
  public static final String TIME_SERIES_FORECAST_ACCURACY = "time-series-compute-accuracy";
  public static final String TIME_SERIES_PREDICT = "time-series-predict";

  private TimeSeriesAnalysisConstants() {
  }
}
