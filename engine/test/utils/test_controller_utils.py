#!/usr/bin/python3

import unittest
from flask.wrappers import Response
from logging import Logger
from exception.engine_computation_exception import EngineComputationException
from utils import controller_utils
from engine_state import Request, RequestType, ReleaseRequestType
from engine_server import setup_server


class TestControllerUtils(unittest.TestCase):


  def test_return_correct_default_response(self) -> None:
    # Given

    # When
    actual_response_body, actual_response_code = controller_utils.build_no_content_response()

    # Then
    self.assertEqual('', actual_response_body)
    self.assertEqual(204, actual_response_code)


  def test_return_correct_response_whe_handling_engine_computation_exception(self) -> None:
    flask_app, _engine, _process_pool, _logger = setup_server(None, None, 'CRITICAL')
    _process_pool.shutdown()
    with flask_app.app_context():
      # Given
      exception = EngineComputationException('Exception Message')

      # When
      actual_response_body, actual_response_code = controller_utils.handle_engine_computation_exception(exception)

      # Then
      self.assertEqual('Exception Message', actual_response_body)
      self.assertEqual(500, actual_response_code)


  def test_return_correct_response_when_request_is_released(self) -> None:
    # Given
    request = Request(1, RequestType.TIME_SERIES_ANALYSIS)
    release_request_type = ReleaseRequestType.RELEASE

    # When
    request.post_release_request(release_request_type)
    actual_response_body, actual_response_code = controller_utils.block_on_release_request_and_return_503(request)

    # Then
    self.assertEqual('1 request dropped', actual_response_body)
    self.assertEqual(503, actual_response_code)


  def test_return_correct_invalid_response_when_release_request_type_is_ignore(self) -> None:
    # Given
    request = Request(1, RequestType.TIME_SERIES_ANALYSIS)
    release_request_type = ReleaseRequestType.IGNORE

    # When
    request.post_release_request(release_request_type)
    actual_response_body, actual_response_code = controller_utils.block_on_release_request_and_return_503(request)

    # Then
    self.assertEqual('This result should not be used', actual_response_body)
    self.assertEqual(-1, actual_response_code)


if __name__ == '__main__':
  unittest.main()
