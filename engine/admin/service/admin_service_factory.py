#!/usr/bin/python3

from admin.service.admin_service import AdminService


class AdminServiceFactory:
  '''
  Builds the admin service
  '''


  def build_service(self) -> AdminService:
    return AdminService()
