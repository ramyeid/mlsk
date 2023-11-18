#!/usr/bin/python3

import unittest
from process_pool.task_result import TaskResult


class MyException(Exception):
  '''
  Custom Exception
  '''


class TestTaskResult(unittest.TestCase):


  def test_should_return_result_if_task_result_is_success(self) -> None:
    # Given
    task_result = TaskResult(False, result='1')

    # When
    actual_result = task_result.get()

    # Then
    self.assertEqual('1', actual_result)


  def test_should_raise_exception_if_task_result_is_fail(self) -> None:
    # Given
    task_result = TaskResult(True, raised_exception=MyException('CustomMyExceptionMessage'))

    # When
    with self.assertRaises(MyException) as context:
      task_result.get()

    # Then
    self.assertEqual('CustomMyExceptionMessage', str(context.exception))


if __name__ == '__main__':
  unittest.main()
