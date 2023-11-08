#!/usr/bin/python3

import argparse
import atexit
import signal
from flask import Flask
from utils.logger import setup_logger, get_logger
from exception.engine_computation_exception import EngineComputationException
import exception_handler
from time_series.controller import time_series_analysis_controller
from classifier.controller import classifier_controller

app = Flask(__name__)

app.add_url_rule('/time-series-analysis/forecast', methods=['POST'],
                 view_func=time_series_analysis_controller.forecast, endpoint='tsa_forecast')
app.add_url_rule('/time-series-analysis/forecast-accuracy', methods=['POST'],
                 view_func=time_series_analysis_controller.compute_accuracy_of_forecast, endpoint='tsa_forecast_accuracy')
app.add_url_rule('/time-series-analysis/predict', methods=['POST'],
                 view_func=time_series_analysis_controller.predict, endpoint='tsa_predict')

app.add_url_rule('/classifier/start', methods=['POST'],
                 view_func=classifier_controller.start, endpoint='dt_start')
app.add_url_rule('/classifier/data', methods=['POST'],
                 view_func=classifier_controller.on_data_received, endpoint='dt_data')
app.add_url_rule('/classifier/predict', methods=['POST'],
                 view_func=classifier_controller.predict, endpoint='dt_predict')
app.add_url_rule('/classifier/predict-accuracy', methods=['POST'],
                 view_func=classifier_controller.compute_accuracy_of_predict, endpoint='dt_predict_accuracy')
app.add_url_rule('/classifier/cancel', methods=['POST'],
                 view_func=classifier_controller.cancel, endpoint='dt_cancel')

app.register_error_handler(EngineComputationException, exception_handler.handle_engine_computation_exception)


def on_shutdown() -> None:
  get_logger().info('Engine will shutdown')


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--port', dest='port', help='port to run python engine', required=True)
  parser.add_argument('--logs-path', dest='logs_path', help='location to dump logs', required=True)
  args = parser.parse_args()

  logger = setup_logger(args.logs_path, args.port)
  atexit.register(on_shutdown)
  signal.signal(signal.SIGTERM, on_shutdown)
  signal.signal(signal.SIGINT, on_shutdown)
  logger.info('Engine is up')
  app.run(host='0.0.0.0', port=args.port)
