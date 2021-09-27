
#!/usr/bin/python3

import unittest
import json
from engine.engine import app
from controller.classifier import decision_tree_controller


test_app = app.test_client()


class TestDecisionTreeController(unittest.TestCase):


  CONTENT_TYPE = 'application/json'
  START_RESOURCE = '/decision-tree/start'
  DATA_RESOURCE = '/decision-tree/data'
  PREDICT_RESOURCE = '/decision-tree/predict'
  PREDICT_ACCURACY_RESOURCE = '/decision-tree/predict-accuracy'


  def setUp(self) -> None:
    decision_tree_controller.classifier_data_builder.reset()


  def assert_on_state(self, expected_prediction_column_name: str, expected_action_column_names: [str], expected_number_of_values: int, expected_data: dict) -> None:
    self.assertEqual(expected_prediction_column_name, decision_tree_controller.classifier_data_builder.get_prediction_column_name())
    self.assertEqual(expected_action_column_names, decision_tree_controller.classifier_data_builder.get_action_column_names())
    self.assertEqual(expected_number_of_values, decision_tree_controller.classifier_data_builder.get_number_of_values())
    self.assertEqual(expected_data, decision_tree_controller.classifier_data_builder.get_data())


  def assert_on_empty_state(self) -> None:
    self.assertIsNone(decision_tree_controller.classifier_data_builder.get_prediction_column_name())
    self.assertIsNone(decision_tree_controller.classifier_data_builder.get_action_column_names())
    self.assertIsNone(decision_tree_controller.classifier_data_builder.get_number_of_values())
    self.assertFalse(decision_tree_controller.classifier_data_builder.get_data())



  def test_set_data_on_start(self) -> None:
    # Given
    body_as_string = build_start_body_as_string()

    # When
    test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_state('Sex', ['Width', 'Height'], 5, {})


  def test_exception_thrown_if_start_data_exists_on_start(self) -> None:
    # Given
    set_start_data('Sex', ['Width', 'Height'], 5)
    body_as_string = build_start_body_as_string()

    # When
    response = test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception EngineComputationException raised while starting decision tree: ' \
            b'Error, Launching start with existing State, Resetting State."\n', response.data)


  def test_exception_thrown_if_data_exists_on_start(self) -> None:
    # Given
    add_data('Sex', [1,2,3,4])
    body_as_string = build_start_body_as_string()

    # When
    response = test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception EngineComputationException raised while starting decision tree: ' \
            b'Error, Launching start with existing State, Resetting State."\n', response.data)


  def test_state_set_on_data(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string()
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    data_body_as_string = build_data_body_as_string()

    # When
    test_app.post(self.DATA_RESOURCE, data=data_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_state('Sex', ['Width', 'Height'], 5, {'Sex': [1, 0, 1, 0 ,0]})


  def test_exception_thrown_if_data_does_not_exists_on_data(self) -> None:
    # Given
    data_body_as_string = build_data_body_as_string()

    # When
    response = test_app.post(self.DATA_RESOURCE, data=data_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception EngineComputationException raised while receiving decision tree data: ' \
            b'Error, Receiving Data without Start, Resetting State."\n', response.data)


  def test_state_is_reset_and_exception_thrown_on_data(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string()
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    failing_data_body = dict(column_name='Sex', values=[1, 0, 1, 0, 0])
    failing_data_body_as_string = json.dumps(failing_data_body)

    # When
    response = test_app.post(self.DATA_RESOURCE, data=failing_data_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception KeyError raised while receiving decision tree data: ' \
            b'\'columnName\'"\n', response.data)


  def test_predict_and_reset_state(self) -> None:
    # Given
    start_body = dict(predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1)
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1])
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0])
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0])
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)

    # When
    response = test_app.post(self.PREDICT_RESOURCE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'{"columnName": "Sex", "values": [0]}', response.data)


  def test_throw_exception_if_start_not_called_on_predict(self) -> None:
    # Given

    # When
    response = test_app.post(self.PREDICT_RESOURCE)

    # Then
    self.assertEqual(b'"Exception EngineComputationException raised while predicting: ' \
            b'Error, No Data was set to launch Decision Tree computation."\n', response.data)


  def test_throw_exception_if_data_not_called_on_predict(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string()
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)

    # When
    response = test_app.post(self.PREDICT_RESOURCE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception EngineComputationException raised while predicting: ' \
            b'Error, No Data was set to launch Decision Tree computation."\n', response.data)


  def test_predict_accuracy_and_reset_state(self) -> None:
    # Given
    start_body = dict(predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1)
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0])
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0])
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0])
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(100.0, float(response.data))


  def test_throw_exception_if_start_not_called_on_predict_accuracy(self) -> None:
    # Given

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE)

    # Then
    self.assertEqual(b'"Exception EngineComputationException raised while computing predict accuracy: ' \
            b'Error, No Data was set to launch Decision Tree computation."\n', response.data)


  def test_throw_exception_if_data_not_called_on_predict_accuracy(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string()
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception EngineComputationException raised while computing predict accuracy: ' \
            b'Error, No Data was set to launch Decision Tree computation."\n', response.data)


  def test_throw_exception_if_actual_values_missing_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1)
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1])
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0])
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0])
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE)

    # Then
    self.assert_on_empty_state()
    self.assertEqual(b'"Exception ClassifierException raised while computing predict accuracy: '\
            b'Error: Actual values are not present."\n', response.data)


def set_start_data(prediction_column_name: str, action_column_names: [str], number_of_values: int) -> None:
  decision_tree_controller.classifier_data_builder.set_start_data(prediction_column_name, action_column_names, number_of_values)


def add_data(column: str, values: [int]) -> None:
  decision_tree_controller.classifier_data_builder.add_data(column, values)


def build_start_body_as_string() -> str:
  body = dict(predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=5)
  return json.dumps(body)


def build_data_body_as_string() -> str:
  data_body = dict(columnName='Sex', values=[1, 0, 1, 0, 0])
  return json.dumps(data_body)
