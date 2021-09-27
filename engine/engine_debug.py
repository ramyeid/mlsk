#!/usr/bin/python3

from argparse import ArgumentParser
from debug.time_series_analysis_engine_debug import launch_time_series_analysis, build_time_series_analysis_arguments
from debug.decision_tree_engine_debug import launch_decision_tree, build_decision_tree_argument


def build_common_arguments(parser: ArgumentParser):
  parser.add_argument("-csv", "--csv", dest="csv_input",
                    help="Location of the csv file to analyze", required=False)
  parser.add_argument("-numberOfValues", "--numberOfValues", dest="number_of_values",
                      help="number of values to apply action", required=False)
  parser.add_argument("-action", "--action", dest="action",
                      help="action to compute: PREDICT / FORECAST / FORECAST_ACCURACY", required=False)
  parser.add_argument("-output", "--output", dest="csv_output",
                      help="Location of the csv output with values", required=False)


if __name__ == "__main__":
  parser = ArgumentParser()
  parser.add_argument("-service", "--service", dest="service",
                      help="algorithm service to launch: TSA", required=True)

  build_common_arguments(parser)
  build_time_series_analysis_arguments(parser)
  build_decision_tree_argument(parser)

  args = parser.parse_args()

  if (args.service == "TSA"):
    launch_time_series_analysis(args)
  elif (args.service == "DT"):
    launch_decision_tree(args)
  else:
    print('None')
