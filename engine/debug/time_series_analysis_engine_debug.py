#!/usr/bin/python3

import sys
import argparse
import pandas as pd
sys.path.append("..")
from services.time_series_analysis_service import TimeSeriesAnalysisService
from utils import csv


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--csv", dest="csv_input",
                        help="Location of the csv file to analyze", required=True)
    parser.add_argument("--dateColumnName", dest="date_column_name",
                        help="Name of the column containing dates", required=True)
    parser.add_argument("--valueColumnName", dest="value_column_name",
                        help="Name of the column containing the values", required=True)
    parser.add_argument("--dateFormat", dest="date_format",
                        help="Date format of the Date values", required=True)
    parser.add_argument("--numberOfValues", dest="number_of_values",
                        help="number of values to predict/forecast", required=True)
    parser.add_argument("--action", dest="action",
                        help="action to compute: FORECAST/ FORECAST_ACCURACY/ PREDICT", required=True)
    parser.add_argument("--output", dest="csv_output",
                        help="Location of the csv output with values", required=False)

    args = parser.parse_args()

    csv_input = str(args.csv_input)
    date_column_name = str(args.date_column_name)
    value_column_name = str(args.value_column_name)
    date_format = str(args.date_format)
    number_of_values = int(args.number_of_values)
    action = str(args.action)
    csv_output = args.csv_output
    if csv_output is None:
        csv_output = args.csv_input.replace(".csv", "_output.csv")

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
        raise Exception("Action not valid: {}. Please should choose between FORECAST/ FORECAST_ACCURACY/ PREDICT"
                        .format(args.action))
