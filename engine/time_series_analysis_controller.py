#!/usr/bin/python3

from flask import request
from utils import csv
from services.time_series_analysis_service import TimeSeriesAnalysisService


def forecast() -> str:
  """
    Forecast values and add the forecasted values to the learning data to predict the next value

    Args:
      - csv (str)               : absolute file path of the csv input file
      - date_column_name (str)  : name of the column that contains the date values
      - value_column_name (str) : name of the column that contains the value that will be predicted
      - date_format (str)       : date format of the date values e.g.: "%Y-%m" (according to Python strftime())
      - number_of_values (int)  : count of values to forecast

    Returns:
      str -> location of the outputFile with forecasted values
  """

  csv_file_path = str(request.form['csv'])
  date_column_name = str(request.form['date_column_name'])
  value_column_name = str(request.form['value_column_name'])
  date_format = str(request.form['date_format'])
  number_of_values = int(request.form['number_of_values'])

  data = csv.read(csv_file_path, date_column_name, date_format)

  time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)
  data_with_predicted_values = time_series_analysis_service.forecast()

  return csv.write(build_output_file_location(csv_file_path, "forecast"), data_with_predicted_values, date_column_name, date_format)


def compute_accuracy_of_forecast() -> float:
  """
    Test forecast with the current values of the csv passed
    Forecast the last {number_of_values} elements and compare them to the actual value
    And compute the accuracy for this algorithm

    Args:
      - csv (str)               : absolute file path of the csv input file
      - date_column_name (str)  : name of the column that contains the date values
      - value_column_name (str) : name of the column that contains the value that will be predicted
      - date_format (str)       : date format of the date values e.g.: "%Y-%m" (according to Python strftime())
      - number_of_values (int)  : count of values to forecast from initial values

    Returns:
      float -> accuracy of the forecast algorithm per centage
  """

  csv_file_path = str(request.form['csv'])
  date_column_name = str(request.form['date_column_name'])
  value_column_name = str(request.form['value_column_name'])
  date_format = str(request.form['date_format'])
  number_of_values = int(request.form['number_of_values'])

  data = csv.read(csv_file_path, date_column_name, date_format)

  time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)
  return str(time_series_analysis_service.compute_forecast_accuracy())


def predict() -> str:
  """
    Predict exact values.

    Args:
      - csv (str)               : absolute file path of the csv input file
      - date_column_name (str)  : name of the column that contains the date values
      - value_column_name (str) : name of the column that contains the value that will be predicted
      - date_format (str)       : date format of the date values e.g.: "%Y-%m" (according to Python strftime())
      - number_of_values (int)  : count of values to predict 

    Returns:
      str -> location of the outputFile with predicted values
  """

  csv_file_path = str(request.form['csv'])
  date_column_name = str(request.form['date_column_name'])
  value_column_name = str(request.form['value_column_name'])
  date_format = str(request.form['date_format'])
  number_of_values = int(request.form['number_of_values'])

  data = csv.read(csv_file_path, date_column_name, date_format)

  time_series_analysis_service = TimeSeriesAnalysisService(data, date_column_name, value_column_name, number_of_values)
  data_with_predicted_values = time_series_analysis_service.predict()

  return csv.write(build_output_file_location(csv_file_path, "predict"), data_with_predicted_values, date_column_name, date_format)


def build_output_file_location(csv_file_path : str, suffix : str) -> str:
  return csv_file_path.replace(".csv", "_" + suffix + "_output.csv")