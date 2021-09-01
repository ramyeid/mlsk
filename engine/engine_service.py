#!/usr/bin/python3

import argparse
import atexit
import signal
from flask import Flask
from engine.utils.logger import setup_logger, get_logger
from engine.exception.engine_computation_exception import EngineComputationException
from engine import time_series_analysis_controller
from engine import exception_handler


app = Flask(__name__)
app.add_url_rule("/time-series-analysis/forecast", methods=['POST'],
                 view_func=time_series_analysis_controller.forecast)
app.add_url_rule("/time-series-analysis/forecast-accuracy", methods=['POST'],
                 view_func=time_series_analysis_controller.compute_accuracy_of_forecast)
app.add_url_rule("/time-series-analysis/predict", methods=['POST'],
                 view_func=time_series_analysis_controller.predict)
app.register_error_handler(EngineComputationException, exception_handler.handle_engine_computation_exception)


def on_shutdown():
  get_logger().info("Engine will shutdown")


if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("--port", dest="port", help="port to run python engine", required=True)
  parser.add_argument("--logs-path", dest="logs_path", help="location to dump logs", required=True)
  args = parser.parse_args()

  logger = setup_logger(args.logs_path, args.port)
  atexit.register(on_shutdown)
  signal.signal(signal.SIGTERM, on_shutdown)
  signal.signal(signal.SIGINT, on_shutdown)
  logger.info("Engine is up")
  app.run(host='0.0.0.0', port=args.port)
