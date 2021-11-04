#!/usr/bin/python3

import unittest
from utils import controller_utils


class TestControllerUtils(unittest.TestCase):


  def test_return_correct_default_response(self) -> None:
    # Given

    # When
    actual_response = controller_utils.build_default_response()

    # Then
    self.assertEqual('{"Status":"Ok"}', actual_response)


if __name__ == '__main__':
  unittest.main()
