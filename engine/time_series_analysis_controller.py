#!/usr/bin/python3

from flask import request
from utils import JsonComplexEncoder
from utils.logger import get_logger
from services.time_series_analysis_service import TimeSeriesAnalysisService
from model.time_series.time_series_analysis_request import TimeSeriesAnalysisRequest
from model.time_series.time_series import TimeSeries
import json


def forecast() -> str:
  """
    Forecast values and add the forecasted values to the learning data to predict the next value

    Args:
      - time_series_analysis_request_json (str) : json corresponding to time_series_analysis_request_json file path of the csv input file

    Returns:
      time_series -> time_series corresponding to the forecasted values and dates.
  """

  try:
    get_logger().info("[Start] forecast request")
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)
    
    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    date_format = time_series.get_date_format()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)

    forecasted_data_frame = time_series_analysis_service.forecast()

    time_series_with_forecasted_values = TimeSeries.from_data_frame(forecasted_data_frame, date_column_name, value_column_name, date_format)
    # only send the elements added.
    time_series_with_forecasted_values_only = TimeSeries(time_series_with_forecasted_values.get_rows()[-number_of_values:],
                                                        date_column_name, value_column_name, date_format)
    
    return json.dumps(time_series_with_forecasted_values_only, cls=JsonComplexEncoder.JsonComplexEncoder)

  except Exception as exception:
    get_logger().error("Exception %s raised while forecasting: %s" % (type(exception).__name__, exception))
    get_logger().exception(exception)
    raise exception

  finally:
    get_logger().info("[End] forecast request")


def compute_accuracy_of_forecast() -> str:
  """
    Test forecast with the current values of the csv passed
    Forecast the last {number_of_values} elements and compare them to the actual value
    And compute the accuracy for this algorithm

    Args:
      - time_series_analysis_request_json (str) : json corresponding to time_series_analysis_request_json file path of the csv input file

    Returns:
      float -> accuracy of the forecast algorithm per centage
  """

  try:
    get_logger().info("[Start] compute forecast accuracy request")
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)

    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)

    return str(time_series_analysis_service.compute_forecast_accuracy())

  except Exception as exception:
    get_logger().error("Exception %s raised while computing forecast accuracy: %s" % (type(exception).__name__, exception))
    get_logger().exception(exception)
    raise exception

  finally:
    get_logger().info("[End] compute forecast accuracy request")


def predict() -> str:
  """
    Predict exact values.

    Args:
      - time_series_analysis_request_json (str) : json corresponding to time_series_analysis_request_json file path of the csv input file

    Returns:
      str -> location of the outputFile with predicted values
  """

  try:
    get_logger().info("[Start] predict request")
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)
    
    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    date_format = time_series.get_date_format()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)

    data_with_predicted_values = time_series_analysis_service.predict()

    time_series_with_predicted_values = TimeSeries.from_data_frame(data_with_predicted_values, date_column_name, value_column_name, date_format)
    #only send the elements added.
    time_series_with_predicted_values_only = TimeSeries(time_series_with_predicted_values.get_rows()[-number_of_values:],
                                                        date_column_name, value_column_name, date_format)
    
    return json.dumps(time_series_with_predicted_values_only, cls=JsonComplexEncoder.JsonComplexEncoder)

  except Exception as exception:
    get_logger().error("Exception %s raised while predicting: %s" % (type(exception).__name__, exception))
    get_logger().exception(exception)
    raise exception

  finally:
    get_logger().info("[End] predict request")