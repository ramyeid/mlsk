#!/usr/bin/python3

from flask import jsonify
from flask.typing import ResponseReturnValue
from engine_state import Request, ReleaseRequestType
from exception.engine_computation_exception import EngineComputationException


def build_no_content_response() -> ResponseReturnValue:
  '''
  Some endpoints do not have a response, but a response is required by Flask.
  The following return value is a contract between the server and the engine.

  Returns
    tuple of empty str and 204 (NoContent) status code
  '''

  return '', 204


def block_on_release_request_and_return_503(request: Request) -> ResponseReturnValue:
  '''
  Wait for a request to be released.

  If a request is eventually released we will return a string containing the request with a 503 (ServiceUnavailable) status code

  Note: This method is most commonly used with multiprocessing_utils#any_of
  '''
  release_request_type = request.get_release_request_rx().recv()
  if release_request_type == ReleaseRequestType.IGNORE:
    return 'This result should not be used', -1
  else:
    return '%s request dropped' % (request.get_request_id()), 503


def unblock_release_with_ignore_on_computation_done(request: Request) -> None:
  '''
  The method `block_on_release_request_and_return_503` will block the thread indefinitely until a data is posted on the release pipe.
  In this method, we are simply posting an ignore message in order to unblock the thread.
  '''
  request.post_release_request(ReleaseRequestType.IGNORE)


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
