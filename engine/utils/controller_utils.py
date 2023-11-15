#!/usr/bin/python3


from flask import jsonify
from flask.typing import ResponseReturnValue
from engine_state import Request
from exception.engine_computation_exception import EngineComputationException


def build_no_content_response() -> ResponseReturnValue:
  '''
  Some endpoints do not have a response, but a response is required by Flask.
  The following return value is a contract between the server and the engine.

  Returns
    tuple of empty str and 204 (NoContent) status code
  '''

  return '', 204


def handle_engine_computation_exception(err: EngineComputationException) -> ResponseReturnValue:
  '''
  Handle EngineComputationException
  This method will help us raise clear exception to other services.

  Arguments
    err - error caught containing message information

  Returns
    json message with error code
  '''
  return '%s' % err, err.code
