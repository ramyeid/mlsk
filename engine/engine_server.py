#!/usr/bin/python3

from typing import Tuple
import argparse
import atexit
import signal
from flask import Flask
from logging import Logger
from utils.logger import LoggerInfo, setup_logger
from utils.controller_utils import handle_engine_computation_exception
from exception.engine_computation_exception import EngineComputationException
from engine_state import Engine
from time_series.controller.time_series_analysis_controller import TimeSeriesAnalysisController
from classifier.controller.classifier_controller import ClassifierController


def on_shutdown(logger: Logger) -> None:
  logger.info('Engine will shutdown')


def setup_server(logger: Logger) -> Tuple[Flask, Engine]:
  # Create Components
  engine = Engine()
  time_series_analysis_controller = TimeSeriesAnalysisController(engine, logger)
  classifier_controller = ClassifierController(engine, logger)

  # Setup Flask Endpoints
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

  app.register_error_handler(EngineComputationException, handle_engine_computation_exception)

  return app, engine


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--port', dest='port', help='port to run python engine', required=True)
  parser.add_argument('--logs-path', dest='logs_path', help='location to dump logs', required=True)
  args = parser.parse_args()

  logger_info = LoggerInfo(args.logs_path, args.port)
  logger = setup_logger(logger_info)

  atexit.register(on_shutdown, logger)
  signal.signal(signal.SIGTERM, on_shutdown)
  signal.signal(signal.SIGINT, on_shutdown)

  flask_app, _engine = setup_server(logger)

  logger.info('Engine is up')
  flask_app.run(host='0.0.0.0', port=args.port)
