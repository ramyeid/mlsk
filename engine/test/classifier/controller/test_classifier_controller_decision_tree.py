
#!/usr/bin/python3

from typing import Any
import unittest
import json
import pandas as pd
from time import sleep
from flask.typing import ResponseReturnValue
from logging import Logger
from threading import Thread
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from engine_server import setup_server
from engine_state import Engine, RequestType
from classifier.service.classifier_service_factory import ClassifierServiceFactory
from classifier.service.classifier_service import IClassifierService
from classifier.model.classifier_type import ClassifierType


class MockClassifierServiceFactory:


  def __init__(self):
    self.build_mock = False


  def do_build_mock(self, block_call_rx: Connection, did_reach_service_tx: Connection):
    self.build_mock = True
    self.block_call_rx = block_call_rx
    self.did_reach_service_tx = did_reach_service_tx


  def do_build_service(self):
    self.build_mock = False


  def build_service(self, classifier_type: ClassifierType,
                    data: pd.DataFrame, action_column_names: [str],
                    prediction_column_name: str, number_of_values: int) -> IClassifierService:
    if self.build_mock:
      return MockClassifierService(self.block_call_rx, self.did_reach_service_tx)
    else:
      return ClassifierServiceFactory().build_service(classifier_type, data, action_column_names, prediction_column_name, number_of_values)


class MockClassifierService:

  def __init__(self, block_call_rx: Connection, did_reach_service_tx: Connection):
    self.block_call_rx = block_call_rx
    self.did_reach_service_tx = did_reach_service_tx


  def predict(self) -> Any:
    self._notify_service_reached_and_block()
    return None


  def compute_predict_accuracy(self) -> Any:
    self._notify_service_reached_and_block()
    return None


  def _notify_service_reached_and_block(self) -> None:
    self.did_reach_service_tx.send('1')
    self.block_call_rx.recv()


class TestClassifierControllerWithDecisionTreeService(unittest.TestCase):


  CONTENT_TYPE = 'application/json'
  START_RESOURCE = '/classifier/start'
  DATA_RESOURCE = '/classifier/data'
  PREDICT_RESOURCE = '/classifier/predict'
  PREDICT_ACCURACY_RESOURCE = '/classifier/predict-accuracy'
  CANCEL_RESOURCE = '/classifier/cancel'


  @classmethod
  def setUpClass(cls) -> None:
    cls.classifier_service_factory = MockClassifierServiceFactory()
    cls.flask_app, cls.engine, cls.process_pool, cls.logger = setup_server(None, None, 'CRITICAL', classifier_service_factory=cls.classifier_service_factory)
    cls.test_app = cls.flask_app.test_client()


  def setUp(self) -> None:
    self.classifier_service_factory.do_build_service()
    self.engine.release_all_inflight_requests()


  @classmethod
  def tearDownClass(cls) -> None:
    cls.process_pool.shutdown()


  def assert_on_response(self, body: str, status_code: int, response: ResponseReturnValue) -> None:
    self.assertEqual(str.encode(body), response.data)
    self.assertEqual(status_code, response.status_code)


  def assert_on_state(self, request_id: int, expected_prediction_column_name: str, expected_action_column_names: [str], expected_number_of_values: int, expected_data: dict) -> None:
    classifier_request = self.engine.get_request(request_id)
    classifier_data = classifier_request.build_classifier_data()
    self.assertEqual(expected_prediction_column_name, classifier_data.get_prediction_column_name())
    self.assertEqual(expected_action_column_names, classifier_data.get_action_column_names())
    self.assertEqual(expected_number_of_values, classifier_data.get_number_of_values())
    self.assertEqual(expected_data, classifier_data.get_data())


  def assert_request_released(self, request_id: int) -> None:
    self.assertFalse(self.engine.contains_request(request_id))


  def test_set_data_on_start(self) -> None:
    # Given
    body_as_string = self.build_start_body_as_string(123)

    # When
    response = self.post_start(body_as_string)

    # Then
    self.assert_on_state(123, 'Sex', ['Width', 'Height'], 5, {})
    self.assert_on_response('', 204, response)


  def test_exception_thrown_if_start_data_exists_on_start(self) -> None:
    # Given
    self.set_start_data(123, 'Sex', ['Width', 'Height'], 5)
    body_as_string = self.build_start_body_as_string(123)

    # When
    response = self.post_start(body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception RequestRegistryException raised while starting decision tree: ' \
      'RequestId (123) already inflight!',
      500,
      response
    )


  def test_exception_thrown_if_data_exists_on_start(self) -> None:
    # Given
    new_request = self.process_pool.get_multiprocessing_manager().Request(123, RequestType.CLASSIFIER)
    self.engine.register_new_request(new_request)
    self.add_data(123, 'Sex', [1,2,3,4])
    body_as_string = self.build_start_body_as_string(123)

    # When
    response = self.post_start(body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception RequestRegistryException raised while starting decision tree: ' \
      'RequestId (123) already inflight!',
      500,
      response
    )


  def test_state_set_on_data(self) -> None:
    # Given
    start_body_as_string = self.build_start_body_as_string(123)
    self.post_start(start_body_as_string)
    data_body_as_string = self.build_data_body_as_string(123)

    # When
    response = self.post_data(data_body_as_string)

    # Then
    self.assert_on_state(123, 'Sex', ['Width', 'Height'], 5, {'Sex': [1, 0, 1, 0 ,0]})
    self.assert_on_response(
      '',
      204,
      response
    )


  def test_exception_thrown_if_start_data_does_not_exists_on_data(self) -> None:
    # Given
    new_request = self.process_pool.get_multiprocessing_manager().Request(123, RequestType.CLASSIFIER)
    self.engine.register_new_request(new_request)
    data_body_as_string = self.build_data_body_as_string(123)

    # When
    response = self.post_data(data_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception EngineComputationException raised while receiving decision tree data: ' \
      'Error, Receiving Data without Start.',
      500,
      response
    )


  def test_predict_and_reset_state(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '{"requestId": 123, "columnName": "Sex", "values": [0], "classifierType": "DECISION_TREE"}',
      200,
      response
    )


  def test_predict_release_request(self) -> None:
    # Given
    block_call_rx, block_call_tx = Pipe()
    did_reach_service_rx, did_reach_service_tx = Pipe()
    self.classifier_service_factory.do_build_mock(block_call_rx, did_reach_service_tx)
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    # Start thread to release a request once received by mock service
    thread = Thread(target=self.release_request_on_reception_after, args=(self.engine, did_reach_service_rx, 123, 1))
    thread.start()
    # Post predict which will hang
    # In the meantime, the thread created above is running and will eventually release the request
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '123 request dropped',
      503,
      response
    )


  def test_throw_exception_if_start_not_called_on_predict(self) -> None:
    # Given
    new_request = self.process_pool.get_multiprocessing_manager().Request(123, RequestType.CLASSIFIER)
    self.engine.register_new_request(new_request)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception EngineComputationException raised while decision tree predicting: ' \
      'Error, No Data was set to launch decision tree computation.',
      500,
      response
    )


  def test_throw_exception_if_data_not_called_on_predict(self) -> None:
    # Given
    start_body_as_string = self.build_start_body_as_string(123)
    self.post_start(start_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception EngineComputationException raised while decision tree predicting: ' \
      'Error, No Data was set to launch decision tree computation.',
      500,
      response
    )


  def test_throw_exception_if_not_all_columns_received_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while decision tree predicting: ' \
      'Error: Column expected ([\'Width\', \'Height\', \'Sex\']) different than received ([\'Width\', \'Height\'])',
      500,
      response
    )


  def test_throw_exception_if_action_columns_do_not_have_same_size_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 1, 1], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while decision tree predicting: ' \
      'Error: Action column sizes are not equal; sizes found: [9, 10]',
      500,
      response
    )


  def test_throw_exception_if_actual_values_missing_greater_than_number_of_values_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = self.post_predict(predict_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while decision tree predicting: ' \
      'Error: Invalid prediction column size. Prediction: 7, Action: 9, Values to predict: 1',
      500,
      response
    )


  def test_predict_accuracy_and_reset_state(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '100.0',
      200,
      response
    )


  def test_predict_accuracy_release_request(self) -> None:
    # Given
    block_call_rx, block_call_tx = Pipe()
    did_reach_service_rx, did_reach_service_tx = Pipe()
    self.classifier_service_factory.do_build_mock(block_call_rx, did_reach_service_tx)
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    # Start thread to release a request once received by mock service
    thread = Thread(target=self.release_request_on_reception_after, args=(self.engine, did_reach_service_rx, 123, 1))
    thread.start()
    # Post forecast which will hang
    # In the meantime, the thread created above is running and will eventually release the request
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '123 request dropped',
      503,
      response
    )


  def test_throw_exception_if_start_not_called_on_predict_accuracy(self) -> None:
    # Given
    new_request = self.process_pool.get_multiprocessing_manager().Request(123, RequestType.CLASSIFIER)
    self.engine.register_new_request(new_request)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception EngineComputationException raised while computing decision tree predict accuracy: ' \
      'Error, No Data was set to launch decision tree computation.',
      500,
      response
    )


  def test_throw_exception_if_data_not_called_on_predict_accuracy(self) -> None:
    # Given
    start_body_as_string = self.build_start_body_as_string(123)
    self.post_start(start_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception EngineComputationException raised while computing decision tree predict accuracy: ' \
      'Error, No Data was set to launch decision tree computation.',
      500,
      response
    )


  def test_throw_exception_if_not_all_columns_received_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while computing decision tree predict accuracy: ' \
      'Error: Column expected ([\'Width\', \'Height\', \'Sex\']) different than received ([\'Sex\', \'Height\'])',
      500,
      response
    )


  def test_throw_exception_if_action_columns_do_not_have_same_size_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while computing decision tree predict accuracy: ' \
      'Error: Action column sizes are not equal; sizes found: [9, 5]',
      500,
      response
    )


  def test_throw_exception_if_actual_values_missing_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    self.post_data(width_data_body_as_string)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    self.post_data(height_data_body_as_string)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = self.post_predict_accuracy(predict_accuracy_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '[123] Exception ClassifierException raised while computing decision tree predict accuracy: ' \
      'Error: Invalid prediction column size. Prediction: 8, Action: 9, Values to predict: 1',
      500,
      response
    )


  def test_reset_state_on_cancel(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    cancel_body = dict(requestId=123, classifierType='DECISION_TREE')
    cancel_body_as_string = json.dumps(cancel_body)

    # When
    response = self.post_cancel(cancel_body_as_string)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '',
      204,
      response
    )


  def test_reset_state_on_cancel_and_start_new_request(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    self.post_start(start_body_as_string)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    self.post_data(sex_data_body_as_string)
    cancel_body = dict(requestId=123, classifierType='DECISION_TREE')
    cancel_body_as_string = json.dumps(cancel_body)

    # When
    response = self.post_cancel(cancel_body_as_string)
    start_body_2 = dict(requestId=123, predictionColumnName='Width', actionColumnNames=['Sex', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_2_as_string = json.dumps(start_body_2)
    self.post_start(start_body_2_as_string)

    # Then
    self.assert_on_state(123, 'Width', ['Sex', 'Height'], 1, {})


  def test_multiple_requests_in_parallel(self) -> None:
    # Given
    # StartBody1
    start_body_1 = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string_1 = json.dumps(start_body_1)
    self.post_start(start_body_as_string_1)
    # Data1
    sex_data_body_1 = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string_1 = json.dumps(sex_data_body_1)
    self.post_data(sex_data_body_as_string_1)
    # StartBody2
    start_body_2 = dict(requestId=999, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string_2 = json.dumps(start_body_2)
    self.post_start(start_body_as_string_2)
    # Data1
    width_data_body_1 = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string_1 = json.dumps(width_data_body_1)
    self.post_data(width_data_body_as_string_1)
    # Data2
    sex_data_body_2 = dict(requestId=999, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string_2 = json.dumps(sex_data_body_2)
    self.post_data(sex_data_body_as_string_2)
    # Data2
    width_data_body_2 = dict(requestId=999, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string_2 = json.dumps(width_data_body_2)
    self.post_data(width_data_body_as_string_2)
    # Data1
    height_data_body_1 = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string_1 = json.dumps(height_data_body_1)
    self.post_data(height_data_body_as_string_1)
    # Data2
    height_data_body_2 = dict(requestId=999, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string_2 = json.dumps(height_data_body_2)
    self.post_data(height_data_body_as_string_2)

    # When1
    predict_accuracy_body_1 = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string_1 = json.dumps(predict_accuracy_body_1)
    response_1 = self.post_predict_accuracy(predict_accuracy_body_as_string_1)
    # When2
    predict_body_2 = dict(requestId=999, classifierType='DECISION_TREE')
    predict_body_as_string_2 = json.dumps(predict_body_2)
    response_2 = self.post_predict(predict_body_as_string_2)

    # Then
    self.assert_request_released(123)
    self.assert_on_response(
      '100.0',
      200,
      response_1
    )
    self.assert_request_released(999)
    self.assert_on_response(
      '{"requestId": 999, "columnName": "Sex", "values": [0], "classifierType": "DECISION_TREE"}',
      200,
      response_2
    )


  @classmethod
  def set_start_data(cls, request_id: int, prediction_column_name: str, action_column_names: [str], number_of_values: int) -> None:
    new_request = cls.process_pool.get_multiprocessing_manager().Request(123, RequestType.CLASSIFIER)
    cls.engine.register_new_request(new_request)
    new_request.set_classifier_start_data(prediction_column_name, action_column_names, number_of_values)


  @classmethod
  def add_data(cls, request_id: int, column: str, values: [int]) -> None:
    cls.engine.get_request(request_id).add_classifier_data(column, values)


  @classmethod
  def build_start_body_as_string(cls, request_id: int) -> str:
    body = dict(requestId=request_id, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=5, classifierType='DECISION_TREE')
    return json.dumps(body)


  @classmethod
  def build_data_body_as_string(cls, request_id: int) -> str:
    data_body = dict(requestId=request_id, columnName='Sex', values=[1, 0, 1, 0, 0], classifierType='DECISION_TREE')
    return json.dumps(data_body)


  @classmethod
  def post_start(cls, body_as_string: str) -> ResponseReturnValue:
    return cls.test_app.post(cls.START_RESOURCE, data=body_as_string, content_type=cls.CONTENT_TYPE)


  @classmethod
  def post_data(cls, body_as_string: str) -> ResponseReturnValue:
    return cls.test_app.post(cls.DATA_RESOURCE, data=body_as_string, content_type=cls.CONTENT_TYPE)


  @classmethod
  def post_predict(cls, body_as_string: str) -> ResponseReturnValue:
    return cls.test_app.post(cls.PREDICT_RESOURCE, data=body_as_string, content_type=cls.CONTENT_TYPE)


  @classmethod
  def post_predict_accuracy(cls, body_as_string: str) -> ResponseReturnValue:
    return cls.test_app.post(cls.PREDICT_ACCURACY_RESOURCE, data=body_as_string, content_type=cls.CONTENT_TYPE)


  @classmethod
  def post_cancel(cls, body_as_string: str) -> ResponseReturnValue:
    return cls.test_app.post(cls.CANCEL_RESOURCE, data=body_as_string, content_type=cls.CONTENT_TYPE)


  @classmethod
  def release_request_on_reception_after(cls,
                                        engine: Engine,
                                        did_reach_service_rx: Connection,
                                        request_id: int,
                                        sleep_time: int) -> None:
    # Wait until request reached the service
    did_reach_service_rx.recv()
    # Sleep to introduce some jitter
    sleep(sleep_time)
    # Now that the request reached the service and the call is stuck, let's release the request
    engine.release_request(request_id)


if __name__ == '__main__':
  unittest.main()
