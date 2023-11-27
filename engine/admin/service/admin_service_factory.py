#!/usr/bin/python3

from engine_state import Engine
from process_pool.process_pool import ProcessPool
from admin.service.admin_service import AdminService


class AdminServiceFactory:
  '''
  Builds the admin service
  '''


  def build_service(self, engine: Engine, process_pool: ProcessPool) -> AdminService:
    return AdminService(engine, process_pool)
