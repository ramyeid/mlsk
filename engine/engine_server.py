#!/usr/bin/python3

from typing import Tuple, Optional
import argparse
import atexit
import signal
from flask import Flask
from logging import Logger
from queue import Queue
from utils.logger import LoggerInfo, setup_logger
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process_pool import ProcessPool
from process_pool.process import ProcessStateHolder
from utils.controller_utils import handle_engine_computation_exception
from exception.engine_computation_exception import EngineComputationException
from engine_state import Request, Engine
from time_series.controller.time_series_analysis_controller import TimeSeriesAnalysisController
from classifier.controller.classifier_controller import ClassifierController


def on_shutdown(logger: Logger) -> None:
  logger.info('Engine will shutdown')


def setup_server(logs_path: Optional[str], port: Optional[str], log_level: str) -> Tuple[Flask, Engine, ProcessPool, Logger]:
  # Create MultiProcessingManager
  # In order to register cross-process shareable objects
  MultiProcessingManager.register('Engine', Engine)
  MultiProcessingManager.register('Request', Request)
  MultiProcessingManager.register('ProcessStateHolder', ProcessStateHolder)
  MultiProcessingManager.register('Queue', Queue)
  MultiProcessingManager.register('LoggerInfo', LoggerInfo)
  multiprocessing_manager = MultiProcessingManager()
  multiprocessing_manager.start()

  # Create Logger
  logger_info = multiprocessing_manager.LoggerInfo(logs_path, port, log_level)
  logger = setup_logger(logger_info)

  # Create & Start ProcessPool
  process_pool = ProcessPool(multiprocessing_manager, 5, 5*2, logger_info)
  process_pool.start()

  # Creat engine state
  engine = multiprocessing_manager.Engine()

  # Create Controllers
  time_series_analysis_controller = TimeSeriesAnalysisController(process_pool, engine, logger)
  classifier_controller = ClassifierController(process_pool, engine, logger)

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

  return app, engine, process_pool, logger


if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument('--port', dest='port', help='port to run python engine', required=True)
  parser.add_argument('--logs-path', dest='logs_path', help='location to dump logs', required=True)
  parser.add_argument('--log-level', dest='log_level', help='logging level', required=True)
  args = parser.parse_args()

  flask_app, _engine, _process_pool, logger = setup_server(args.logs_path, args.port, args.log_level)

  atexit.register(on_shutdown, logger)
  signal.signal(signal.SIGTERM, on_shutdown)
  signal.signal(signal.SIGINT, on_shutdown)

  logger.info('Engine is up')
  flask_app.run(host='0.0.0.0', port=args.port)
