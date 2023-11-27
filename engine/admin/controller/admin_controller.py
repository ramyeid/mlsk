#!/usr/bin/python3

import json
from flask import request
from flask.typing import ResponseReturnValue
from logging import Logger
from process_pool.process_pool import ProcessPool
from engine_state import Engine
from utils.json_complex_encoder import JsonComplexEncoder
from exception.engine_computation_exception import EngineComputationException
from admin.service.admin_service_factory import AdminServiceFactory


class AdminController:


  def __init__(self,
              admin_service_factory: AdminServiceFactory,
              engine: Engine,
              process_pool: ProcessPool,
              logger: Logger):
    self.admin_service = admin_service_factory.build_service(engine, process_pool)
    self.engine = engine
    self.process_pool = process_pool
    self.logger = logger


  def ping(self) -> ResponseReturnValue:
    '''
    A simple ping request that will also return the status of the engine and its processes

    Arguments
      [None]
    '''
    try:
      self.logger.info('[Start][Admin] Ping')

      engine_details = self.admin_service.ping()

      return json.dumps(engine_details, cls=JsonComplexEncoder)

    except Exception as exception:
      error_message = '[Admin] Exception %s raised while pinging engine: %s' % (type(exception).__name__, exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.logger.info('[End][Admin] Ping')


  def release_request(self) -> ResponseReturnValue:
    pass


  def stop_process(self) -> ResponseReturnValue:
    pass


  def start_process(self) -> ResponseReturnValue:
    pass


  def restart_process(self) -> ResponseReturnValue:
    pass


  def restart_engine(self) -> ResponseReturnValue:
    pass

