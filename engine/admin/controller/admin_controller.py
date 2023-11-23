#!/usr/bin/python3

import json
from flask import request
from logging import Logger
from process_pool.process_pool import ProcessPool
from engine_state import Engine
from utils.json_complex_encoder import JsonComplexEncoder
from admin.service.admin_service_factory import AdminServiceFactory
from admin import model

class AdminController:


  def __init__(self,
              admin_service_factory: AdminServiceFactory,
              engine: Engine,
              process_pool: ProcessPool,
              logger: Logger):
    self.admin_service_factory = admin_service_factory
    self.engine = engine
    self.process_pool = process_pool
    self.logger = logger


  def ping(self) -> model.engine.Engine:
    pass


  def release_request(self) -> str:
    pass


  def stop_process(self) -> None:
    pass


  def start_process(self) -> None:
    pass


  def restart_process(self) -> str:
    pass


  def restart_engine(self) -> str:
    pass

