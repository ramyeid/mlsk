#!/usr/bin/python3

import unittest
from flask.wrappers import Response
from logging import Logger
from exception.engine_computation_exception import EngineComputationException
from utils import controller_utils
from engine_state import Request, RequestType
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
    logger = Logger('Test')
    logger.setLevel('CRITICAL')
    flask_app, _engine = setup_server(logger)
    with flask_app.app_context():
      # Given
      exception = EngineComputationException('Exception Message')

      # When
      actual_response_body, actual_response_code = controller_utils.handle_engine_computation_exception(exception)

      # Then
      self.assertEqual('Exception Message', actual_response_body)
      self.assertEqual(500, actual_response_code)


if __name__ == '__main__':
  unittest.main()
