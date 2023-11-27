#!/usr/bin/python3

import logging
from logging import Logger


class LoggerInfo:
  '''
  Wrapper over information needed to setup the logger.
  Helpful when dealing with multiprocessing,
  since we will need to re-create the logger in each subprocess.

  Attributes
    logs_path (str) - Pointer to the path where to dump the log
    port      (str) - Current port used by the engine
    level     (int) - Level of log
  '''

  def __init__(self, logs_path: str, port: str, level: int=logging.INFO):
    self.logs_path = logs_path
    self.port = port
    self.level = level


  def get_logs_path(self) -> str:
    return self.logs_path


  def get_port(self) -> str:
    return self.port


  def get_level(self) -> int:
        return self.level


def __create_file_handler(logger_info: LoggerInfo) -> None:
  if logger_info.get_logs_path() is not None and len(logger_info.get_logs_path()) != 0:
    logger_file_handler = logging.FileHandler('%s/engine-%s.log' % (logger_info.get_logs_path(), logger_info.get_port()))
    default_formatter = logging.Formatter('[%(asctime)s] [%(levelname)s] [%(funcName)s():%(lineno)s] - %(message)s',
                                          '%d-%m-%Y %H:%M:%S')
    logger_file_handler.setFormatter(default_formatter)
    return logger_file_handler
  return None


def setup_logger(logger_info: LoggerInfo) -> Logger:
  file_handler = __create_file_handler(logger_info)

  logger = logging.getLogger('Engine')
  logger.setLevel(logger_info.get_level())
  if file_handler is not None:
    logger.addHandler(file_handler)
  return logger
