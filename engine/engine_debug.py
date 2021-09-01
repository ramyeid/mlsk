#!/usr/bin/python3

from argparse import ArgumentParser
from debug.time_series_analysis_engine_debug import launch_time_series_analysis, build_time_series_analysis_arguments

if __name__ == "__main__":
  parser = ArgumentParser()
  parser.add_argument("--service", dest="service",
                      help="algorithm service to launch: TSA", required=True)

  build_time_series_analysis_arguments(parser)

  args = parser.parse_args()

  if (args.service == "TSA"):
    launch_time_series_analysis(args)
  else:
    print('None')
