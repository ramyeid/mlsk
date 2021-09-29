#!/usr/bin/python3

from argparse import ArgumentParser, Namespace
import pandas as pd
from utils import csv
from debug.debug_utils import throw_exception_if_argument_null, get_or_create_output_file
from debug.engine_debug_exception import build_action_not_valid_exception
from time_series.service.time_series_analysis_service import TimeSeriesAnalysisService


def build_time_series_analysis_arguments(parser: ArgumentParser) -> None:
  parser.add_argument('-dateColumnName', '--dateColumnName', dest='date_column_name',
                      help='Name of the column containing dates', required=False)
  parser.add_argument('-valueColumnName', '--valueColumnName', dest='value_column_name',
                      help='Name of the column containing the values', required=False)
  parser.add_argument('-dateFormat', '--dateFormat', dest='date_format',
                      help='Date format of the Date values', required=False)


def launch_time_series_analysis(args: Namespace) -> None:
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

  if action == 'FORECAST':
    forecasted_data_frame = time_series_analysis_service.forecast()
    result_data_frame = pd.concat([data_frame, forecasted_data_frame], ignore_index=True, sort=False)
    print(csv.write(csv_output, result_data_frame, date_column_name, date_format))

  elif action == 'FORECAST_ACCURACY':
    print('Accuracy: {} %'.format(time_series_analysis_service.compute_forecast_accuracy()))

  elif action == 'PREDICT':
    prediction_data_frame = time_series_analysis_service.predict()
    result_data_frame = pd.concat([data_frame, prediction_data_frame], ignore_index=True, sort=False)
    print(csv.write(csv_output, result_data_frame, date_column_name, date_format))

  else:
    raise build_action_not_valid_exception(['FORECAST', 'FORECAST_ACCURACY', 'PREDICT'], args.action)
