#!/usr/bin/python3

import sys
from argparse import ArgumentParser, Namespace
import pandas as pd
from engine.services.time_series_analysis_service import TimeSeriesAnalysisService
from engine.utils import csv
from engine.debug.debug_utils import throw_exception_if_argument_null
from engine.debug.engine_debug_exception import EngineDebugException


def build_time_series_analysis_arguments(parser: ArgumentParser):
  parser.add_argument("--csv", dest="csv_input",
                      help="Location of the csv file to analyze", required=False)
  parser.add_argument("--dateColumnName", dest="date_column_name",
                      help="Name of the column containing dates", required=False)
  parser.add_argument("--valueColumnName", dest="value_column_name",
                      help="Name of the column containing the values", required=False)
  parser.add_argument("--dateFormat", dest="date_format",
                      help="Date format of the Date values", required=False)
  parser.add_argument("--numberOfValues", dest="number_of_values",
                      help="number of values to predict/forecast", required=False)
  parser.add_argument("--action", dest="action",
                      help="action to compute: FORECAST/ FORECAST_ACCURACY/ PREDICT", required=False)
  parser.add_argument("--output", dest="csv_output",
                      help="Location of the csv output with values", required=False)


def launch_time_series_analysis(args: Namespace):
  throw_exception_if_argument_null('csv_input', args.csv_input)
  throw_exception_if_argument_null('date_column_name', args.date_column_name)
  throw_exception_if_argument_null('value_column_name', args.value_column_name)
  throw_exception_if_argument_null('date_format', args.date_format)
  throw_exception_if_argument_null('number_of_values', args.number_of_values)
  throw_exception_if_argument_null('action', args.action)

  csv_input = str(args.csv_input)
  date_column_name = str(args.date_column_name)
  value_column_name = str(args.value_column_name)
  date_format = str(args.date_format)
  number_of_values = int(args.number_of_values)
  action = str(args.action)
  csv_output = get_or_create_output_file(args)

  data_frame = csv.read(csv_input, date_column_name, date_format)
  time_series_analysis_service = TimeSeriesAnalysisService(data_frame, date_column_name,
                                                            value_column_name, number_of_values)

  if action == "FORECAST":
    forecasted_data_frame = time_series_analysis_service.forecast()
    result_data_frame = pd.concat([data_frame, forecasted_data_frame], ignore_index=True, sort=False)
    print(csv.write(csv_output, result_data_frame, date_column_name, date_format))

  elif action == "FORECAST_ACCURACY":
    print("Accuracy: {} %".format(time_series_analysis_service.compute_forecast_accuracy()))

  elif action == "PREDICT":
    prediction_data_frame = time_series_analysis_service.predict()
    result_data_frame = pd.concat([data_frame, prediction_data_frame], ignore_index=True, sort=False)
    print(csv.write(csv_output, result_data_frame, date_column_name, date_format))

  else:
    raise EngineDebugException("Action not valid: {}. Please choose between FORECAST/ FORECAST_ACCURACY/ PREDICT"
                    .format(args.action))


def get_or_create_output_file(args: Namespace):
  if args.csv_output is None:
    return args.csv_input.replace(".csv", "_output.csv")

  return args.csv_output
