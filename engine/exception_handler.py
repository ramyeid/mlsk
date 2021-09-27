#!/usr/bin/python3

from flask import jsonify
from flask.typing import ResponseReturnValue
from exception.engine_computation_exception import EngineComputationException


def handle_engine_computation_exception(err: EngineComputationException) -> ResponseReturnValue:
  """
  Handle EngineComputationException
  This method will help us raise clear exception to other services.

  Arguments
    err - error caught containing message information

  Returns
    json message with error code
  """
  return jsonify("%s" % err), err.code
