#!/usr/bin/python3

import unittest
from flask.wrappers import Response
from engine.engine import app
from exception_handler import handle_engine_computation_exception
from exception.engine_computation_exception import EngineComputationException

test_app = app


class TestExceptionHandler(unittest.TestCase):


    def test_handle_engine_computation_exception(self):
        with test_app.app_context():
            # Given
            exception = EngineComputationException("Exception Message")

            # When
            actual_result = handle_engine_computation_exception(exception)

            # Then
            assert 500 == actual_result[1]
            assert isinstance(actual_result[0], Response)


if __name__ == "__main__":
    unittest.main()
