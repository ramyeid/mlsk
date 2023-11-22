#!/usr/bin/python3

import unittest
import threading
from engine_state import Engine, Request, RequestType, ReleaseRequestType, RequestRegistryException
from classifier.registry.classifier_data_builder import ClassifierDataBuilder, ClassifierData


def assert_on_release_pipe_content(request: Request, release_request_type: ReleaseRequestType) -> None:
  assert release_request_type == request.get_release_request_rx().recv()


class TestEngineState(unittest.TestCase):


  def test_register_new_request(self) -> None:
    # Given
    engine = Engine()
    engine.register_new_request(Request(123, RequestType.CLASSIFIER))

    # When
    request_count = engine.request_count()
    contains_123 = engine.contains_request(123)
    request = engine.get_request(123)
    contains_classifier_start_data = request.contains_classifier_start_data()
    contains_classifier_data = request.contains_clasifier_data()

    # Then
    self.assertEqual(1, request_count)
    self.assertTrue(contains_123)
    self.assertFalse(contains_classifier_start_data)
    self.assertFalse(contains_classifier_data)


  def test_register_new_request_throws_if_id_already_inflight(self) -> None:
    # Given
    engine = Engine()
    engine.register_new_request(Request(123, RequestType.TIME_SERIES_ANALYSIS))

    # When
    with self.assertRaises(RequestRegistryException) as context:
      engine.register_new_request(Request(123, RequestType.TIME_SERIES_ANALYSIS))

    # Then
    self.assertEqual('RequestId (123) already inflight!', str(context.exception))


  def test_get_request_throws_if_request_id_does_not_exist(self) -> None:
    # Given
    engine = Engine()

    # When
    with self.assertRaises(RequestRegistryException) as context:
      engine.get_request(123)

    # Then
    self.assertEqual('RequestId (123) not inflight!', str(context.exception))


  def test_contains_request_returns_bool_if_request_is_inflight(self) -> None:
    # Given
    engine = Engine()

    # When
    contains_123_pre_register = engine.contains_request(123)
    engine.register_new_request(Request(123, RequestType.CLASSIFIER))
    contains_123_post_register = engine.contains_request(123)

    # Then
    self.assertFalse(contains_123_pre_register)
    self.assertTrue(contains_123_post_register)


  def test_release_request_returns_false_if_no_request_to_release(self) -> None:
    # Given
    engine = Engine()

    # When
    did_release_request = engine.release_request(123)

    # Then
    self.assertFalse(did_release_request)


  def test_release_request_removes_from_map_and_post_release_to_pipe_tx(self) -> None:
    # Given
    engine = Engine()
    request = Request(123, RequestType.TIME_SERIES_ANALYSIS)
    engine.register_new_request(request)
    count_pre_release = engine.request_count()
    contains_123_pre_release = engine.contains_request(123)
    thread = threading.Thread(target=assert_on_release_pipe_content, args=([request, ReleaseRequestType.RELEASE]))

    # When
    thread.start()
    did_release_request = engine.release_request(123)
    # should be unblocked instantly
    thread.join()

    # Then
    count_post_release = engine.request_count()
    contains_123_post_release = engine.contains_request(123)
    self.assertEqual(1, count_pre_release)
    self.assertTrue(contains_123_pre_release)
    self.assertEqual(0, count_post_release)
    self.assertFalse(contains_123_post_release)


  def test_release_all_inflight_requets_and_post_release_to_pipe(self) -> None:
    # Given
    engine = Engine()
    request1 = Request(123, RequestType.TIME_SERIES_ANALYSIS)
    request2 = Request(124, RequestType.CLASSIFIER)
    engine.register_new_request(request1)
    engine.register_new_request(request2)
    contains_123_pre_reset = engine.contains_request(123)
    contains_124_pre_reset = engine.contains_request(124)
    count_pre_reset = engine.request_count()
    thread1 = threading.Thread(target=assert_on_release_pipe_content, args=([request1, ReleaseRequestType.RELEASE]))
    thread2 = threading.Thread(target=assert_on_release_pipe_content, args=([request2, ReleaseRequestType.RELEASE]))

    # When
    thread1.start()
    thread2.start()
    engine.release_all_inflight_requests()
    thread1.join()
    thread2.join()

    # Then
    count_post_reset = engine.request_count()
    contains_123_post_reset = engine.contains_request(123)
    contains_124_post_reset = engine.contains_request(124)
    self.assertEqual(2, count_pre_reset)
    self.assertTrue(contains_123_pre_reset)
    self.assertTrue(contains_124_pre_reset)
    self.assertEqual(0, count_post_reset)
    self.assertFalse(contains_123_post_reset)
    self.assertFalse(contains_124_post_reset)


class TestRequest(unittest.TestCase):


  def test_build_classifier_data_using_request(self) -> None:
    # Given
    request = Request(124, RequestType.CLASSIFIER)

    # When
    contains_classifier_data_pre_start = request.contains_clasifier_data()
    contains_classifier_start_data_pre_start = request.contains_classifier_start_data()
    request.set_classifier_start_data('pred', ['col0', 'col1'], 2)
    request.add_classifier_data('col0', [0, 1, 0, 1, 1])
    contains_classifier_data_post_data = request.contains_clasifier_data()
    contains_classifier_start_data_post_data = request.contains_classifier_start_data()
    request.add_classifier_data('col1', [1, 1, 1, 1, 1])
    actual_classifier_data = request.build_classifier_data()
    contains_classifier_data_post_build = request.contains_clasifier_data()
    contains_classifier_start_data_post_build = request.contains_classifier_start_data()

    # Then
    expected_dict = {'col0': [0, 1, 0, 1, 1], 'col1':[1, 1, 1, 1, 1]}
    expected_classifier_data = ClassifierData('pred', ['col0', 'col1'], 2, expected_dict)
    self.assertEqual(expected_classifier_data, actual_classifier_data)
    self.assertFalse(contains_classifier_data_pre_start)
    self.assertFalse(contains_classifier_start_data_pre_start)
    self.assertTrue(contains_classifier_data_post_data)
    self.assertTrue(contains_classifier_start_data_post_data)
    self.assertTrue(contains_classifier_data_post_build)
    self.assertTrue(contains_classifier_start_data_post_build)


  def test_set_classifier_start_data_throws_exception_if_request_type_is_not_classifier(self) -> None:
    # Given
    request = Request(124, RequestType.TIME_SERIES_ANALYSIS)

    # When
    with self.assertRaises(RequestRegistryException) as context:
      request.set_classifier_start_data('pred', ['col0', 'col1'], 2)

    # Then
    self.assertEqual('Unable to set classifier start data on `TIME_SERIES_ANALYSIS` request', str(context.exception))


  def test_set_classifier_data_throws_exception_if_request_type_is_not_classifier(self) -> None:
    # Given
    request = Request(124, RequestType.TIME_SERIES_ANALYSIS)

    # When
    with self.assertRaises(RequestRegistryException) as context:
      request.add_classifier_data('col0', [0, 1, 0, 1, 1])

    # Then
    self.assertEqual('Unable to add classifier data on `TIME_SERIES_ANALYSIS` request', str(context.exception))


  def test_build_classifier_data_throws_exception_if_request_type_is_not_classifier(self) -> None:
    # Given
    request = Request(124, RequestType.TIME_SERIES_ANALYSIS)

    # When
    with self.assertRaises(RequestRegistryException) as context:
      request.build_classifier_data()

    # Then
    self.assertEqual('Unable to build classifier data on `TIME_SERIES_ANALYSIS` request', str(context.exception))


  def test_contains_classifier_data_returns_false_if_request_type_is_not_classifier(self) -> None:
    # Given
    request = Request(124, RequestType.TIME_SERIES_ANALYSIS)

    # When
    contains_classifier_data = request.contains_clasifier_data()

    # Then
    self.assertFalse(contains_classifier_data)


  def test_contains_classifier_start_data_returns_false_if_request_type_is_not_classifier(self) -> None:
    # Given
    request = Request(124, RequestType.TIME_SERIES_ANALYSIS)

    # When
    contains_classifier_data = request.contains_classifier_start_data()

    # Then
    self.assertFalse(contains_classifier_data)


  def test_release_request_rx_should_complete_when_posting_release_request_on_tx(self) -> None:
    # Given
    request = Request(123, RequestType.TIME_SERIES_ANALYSIS)
    release_request_type = ReleaseRequestType.RELEASE
    thread = threading.Thread(target=assert_on_release_pipe_content, args=([request, ReleaseRequestType.RELEASE]))

    # When
    thread.start()
    request.post_release_request(release_request_type)
    # should be unblocked instantly
    thread.join()

    # Then
    self.assertTrue(True)


if __name__ == '__main__':
  unittest.main()
