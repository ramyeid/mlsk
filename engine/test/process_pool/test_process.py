#!/usr/bin/python3

import unittest
from queue import Queue
from process_pool.multiprocessing_manager import MultiProcessingManager
from process_pool.process import Process, ProcessStateHolder, ProcessState


class TestProcess(unittest.TestCase):


  def test_should_correctly_flip_process_status(self) -> None:
    # Given
    process_state_holder = ProcessStateHolder()
    process = Process(process_state_holder, Queue())

    # When
    actual_start_state = process.get_state()
    process_state_holder.to_busy()
    process_state_holder.to_idle()
    process_state_holder.to_idle()
    process_state_holder.to_busy()
    process_state_holder.to_busy()
    actual_end_state = process.get_state()
    actual_process_state_holder = process.get_state_holder()

    # Then
    self.assertEqual(ProcessState.IDLE, actual_start_state)
    self.assertEqual(ProcessState.BUSY, actual_end_state)
    self.assertEqual(ProcessState.BUSY, actual_process_state_holder.get())
    self.assertEqual(5, actual_process_state_holder.get_flip_flop_count())


  def test_should_correctly_start_and_stop_process(self) -> None:
    # Given
    MultiProcessingManager.register('ProcessStateHolder', ProcessStateHolder)
    MultiProcessingManager.register('Queue', Queue)
    multiprocessing_manager = MultiProcessingManager()
    multiprocessing_manager.start()
    process = Process(multiprocessing_manager.ProcessStateHolder(), multiprocessing_manager.Queue())

    # When
    process.start()
    is_process_alive_pre_terminate = process.multiprocessing_process.is_alive()
    process.terminate()

    # Then
    self.assertTrue(is_process_alive_pre_terminate)


if __name__ == '__main__':
  unittest.main()
