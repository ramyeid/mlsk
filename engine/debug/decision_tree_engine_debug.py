#!/usr/bin/python3

from argparse import ArgumentParser, Namespace
import pandas as pd
from services.classifier.decision_tree_service import DecisionTreeService
from utils import csv
from debug.debug_utils import throw_exception_if_argument_null, get_or_create_output_file
from debug.engine_debug_exception import build_action_not_valid_exception


def build_decision_tree_argument(parser: ArgumentParser):
  parser.add_argument("-actionColumnNames", "--actionColumnNames", dest="action_column_names",
                      help="Names of the action column (comma seperated)", required=False)
  parser.add_argument("-predictionColumnName", "--predictionColumnName", dest="prediction_column_name",
                      help="Name of the column to predict", required=False)


def launch_decision_tree(args: Namespace):
  throw_exception_if_argument_null('csv_input', args.csv_input)
  throw_exception_if_argument_null('action_column_names', args.action_column_names)
  throw_exception_if_argument_null('prediction_column_name', args.prediction_column_name)
  throw_exception_if_argument_null('number_of_values', args.number_of_values)
  throw_exception_if_argument_null('action', args.action)

  csv_input = str(args.csv_input)
  action_column_names = str(args.action_column_names).split(",")
  prediction_column_name = str(args.prediction_column_name)
  number_of_values = int(args.number_of_values)
  action = str(args.action)
  csv_output = get_or_create_output_file(args)

  data_frame = csv.read(csv_input, [*[prediction_column_name], *action_column_names])

  decision_tree_service = DecisionTreeService(data_frame, action_column_names, prediction_column_name, number_of_values)

  if action == "PREDICT":
    predicted_data_frame = decision_tree_service.predict()

    data_frame[prediction_column_name+'_Predicted'] = build_predicted_and_actual_values(data_frame, predicted_data_frame, prediction_column_name, number_of_values)

    print(csv.write(csv_output, data_frame))

  elif action == "PREDICT_ACCURACY":
    print("Accuracy: {} %".format(decision_tree_service.compute_predict_accuracy()))

  else:
    raise build_action_not_valid_exception(["PREDICT", "PREDICT_ACCURACY"], args.action)


def build_predicted_and_actual_values(data_frame: pd.DataFrame, predicted_data_frame: pd.DataFrame, prediction_column_name: str, number_of_values: int) -> [float]:
  predicted_and_actual = []

  for i in range(len(data_frame[prediction_column_name]) - number_of_values):
    predicted_and_actual.append(data_frame[prediction_column_name][i])

  for i in range(number_of_values):
    predicted_and_actual.append(predicted_data_frame[prediction_column_name][i])

  return predicted_and_actual
