#!/usr/bin/python3

import json
from flask import request
from engine_state import get_engine
from utils.json_complex_encoder import JsonComplexEncoder
from utils.logger import get_logger
from exception.engine_computation_exception import EngineComputationException
from time_series.service.time_series_analysis_service import TimeSeriesAnalysisService
from time_series.model.time_series_analysis_request import TimeSeriesAnalysisRequest
from time_series.model.time_series import TimeSeries


def forecast() -> str:
  '''
  Forecast values and add the forecasted values to the learning data to predict the next value

  Arguments
    time_series_analysis_request_json (str) - json corresponding to TimeSeriesAnalysisRequest
                                              containing time series rows, column names and number of values

  Returns
    time_series -> time_series corresponding to the forecasted values and dates.
  '''

  request_id = None
  try:
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)
    request_id = time_series_analysis_request.get_request_id()

    get_logger().info('[Start][%d] forecast request', request_id)

    get_engine().register_new_time_series_request(request_id)

    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    date_format = time_series.get_date_format()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name,
                                                              value_column_name, number_of_values)

    forecasted_data_frame = time_series_analysis_service.forecast()

    forecasted_time_series = TimeSeries.from_data_frame(forecasted_data_frame, date_column_name,
                                                        value_column_name, date_format)

    return json.dumps(forecasted_time_series, cls=JsonComplexEncoder)

  except Exception as exception:
    error_message = '[%s] Exception %s raised while forecasting: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_engine().release_request(request_id)
    get_logger().info('[End][%d] forecast request', request_id)


def compute_accuracy_of_forecast() -> str:
  '''
  Test forecast with the current values of the csv passed
  Forecast the last {number_of_values} elements and compare them to the actual value
  And compute the accuracy for this algorithm

  Arguments
    time_series_analysis_request_json (str) - json corresponding to TimeSeriesAnalysisRequest
                                              containing time series rows, column names and number of values

  Returns
    float -> accuracy of the forecast algorithm percentage
  '''

  request_id = None
  try:
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)
    request_id = time_series_analysis_request.get_request_id()

    get_logger().info('[Start][%d] compute forecast accuracy request', request_id)

    get_engine().register_new_time_series_request(request_id)

    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name,
                                                              value_column_name, number_of_values)

    return str(time_series_analysis_service.compute_forecast_accuracy())

  except Exception as exception:
    error_message = '[%s] Exception %s raised while computing forecast accuracy: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_engine().release_request(request_id)
    get_logger().info('[End][%d] compute forecast accuracy request', request_id)


def predict() -> str:
  '''
  Predict exact values.

  Arguments
    time_series_analysis_request_json (str) - json corresponding to TimeSeriesAnalysisRequest
                                              containing time series rows, column names and number of values

  Returns
    time_series -> time_series corresponding to the predicted values and dates.
  '''

  request_id = None
  try:
    time_series_analysis_request = TimeSeriesAnalysisRequest.from_json(request.json)
    request_id = time_series_analysis_request.get_request_id()

    get_logger().info('[Start][%d] predict request', request_id)

    get_engine().register_new_time_series_request(request_id)

    time_series = time_series_analysis_request.get_time_series()
    data = time_series.to_data_frame()
    date_column_name = time_series.get_date_column_name()
    value_column_name = time_series.get_value_column_name()
    number_of_values = time_series_analysis_request.get_number_of_values()
    date_format = time_series.get_date_format()
    time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name,
                                                              number_of_values)

    predicted_data_frame = time_series_analysis_service.predict()

    predicted_time_series = TimeSeries.from_data_frame(predicted_data_frame, date_column_name,
                                                        value_column_name, date_format)

    return json.dumps(predicted_time_series, cls=JsonComplexEncoder)

  except Exception as exception:
    error_message = '[%s] Exception %s raised while predicting: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_engine().release_request(request_id)
    get_logger().info('[End][%d] predict request', request_id)
