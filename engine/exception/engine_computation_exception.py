#!/usr/bin/python3

from http.client import HTTPException


class EngineComputationException(HTTPException):
  """
  Exception that will be thrown by services.
  This exception has a specific handler that maps the error to a Response
  """
  code = 500
