
#!/usr/bin/python3

import unittest
import json
from engine.engine import app
from classifier.registry.classifier_data_registry_exception import ClassifierDataRegistryException
from classifier.controller import classifier_controller


test_app = app.test_client()


class TestClassifierControllerWithDecisionTreeService(unittest.TestCase):


  CONTENT_TYPE = 'application/json'
  START_RESOURCE = '/classifier/start'
  DATA_RESOURCE = '/classifier/data'
  PREDICT_RESOURCE = '/classifier/predict'
  PREDICT_ACCURACY_RESOURCE = '/classifier/predict-accuracy'
  CANCEL_RESOURCE = '/classifier/cancel'


  def setUp(self) -> None:
    classifier_controller.classifier_data_builder_registry.reset()


  def assert_on_state(self, request_id: int, expected_prediction_column_name: str, expected_action_column_names: [str], expected_number_of_values: int, expected_data: dict) -> None:
    classifier_data_builder = classifier_controller.classifier_data_builder_registry.get_builder(request_id)
    self.assertEqual(expected_prediction_column_name, classifier_data_builder.get_prediction_column_name())
    self.assertEqual(expected_action_column_names, classifier_data_builder.get_action_column_names())
    self.assertEqual(expected_number_of_values, classifier_data_builder.get_number_of_values())
    self.assertEqual(expected_data, classifier_data_builder.get_data())


  def assert_request_cancelled(self, request_id: int) -> None:
    self.assertFalse(classifier_controller.classifier_data_builder_registry.contains_builder(request_id))


  def test_set_data_on_start(self) -> None:
    # Given
    body_as_string = build_start_body_as_string(123)

    # When
    response = test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_state(123, 'Sex', ['Width', 'Height'], 5, {})
    self.assertEqual(b'{"Status":"Ok"}', response.data)


  def test_exception_thrown_if_start_data_exists_on_start(self) -> None:
    # Given
    set_start_data(123, 'Sex', ['Width', 'Height'], 5)
    body_as_string = build_start_body_as_string(123)

    # When
    response = test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception EngineComputationException raised while starting decision tree: ' \
            b'Error, Launching start with existing State, Resetting State."\n', response.data)


  def test_exception_thrown_if_data_exists_on_start(self) -> None:
    # Given
    classifier_controller.classifier_data_builder_registry.new_builder(123)
    add_data(123, 'Sex', [1,2,3,4])
    body_as_string = build_start_body_as_string(123)

    # When
    response = test_app.post(self.START_RESOURCE, data=body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception EngineComputationException raised while starting decision tree: ' \
            b'Error, Launching start with existing State, Resetting State."\n', response.data)


  def test_state_set_on_data(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string(123)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    data_body_as_string = build_data_body_as_string(123)

    # When
    response = test_app.post(self.DATA_RESOURCE, data=data_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_state(123, 'Sex', ['Width', 'Height'], 5, {'Sex': [1, 0, 1, 0 ,0]})
    self.assertEqual(b'{"Status":"Ok"}', response.data)


  def test_exception_thrown_if_data_does_not_exists_on_data(self) -> None:
    # Given
    data_body_as_string = build_data_body_as_string(123)

    # When
    response = test_app.post(self.DATA_RESOURCE, data=data_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception EngineComputationException raised while receiving decision tree data: ' \
            b'Error, Receiving Data without Start, Resetting State."\n', response.data)


  def test_predict_and_reset_state(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'{"requestId": 123, "columnName": "Sex", "values": [0], "classifierType": "DECISION_TREE"}', response.data)


  def test_throw_exception_if_start_not_called_on_predict(self) -> None:
    # Given
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assertEqual(b'"[123] Exception EngineComputationException raised while decision tree predicting: ' \
            b'Error, No Data was set to launch decision tree computation."\n', response.data)


  def test_throw_exception_if_data_not_called_on_predict(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string(123)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception EngineComputationException raised while decision tree predicting: ' \
            b'Error, No Data was set to launch decision tree computation."\n', response.data)


  def test_throw_exception_if_not_all_columns_received_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    expected_exception = b'"[123] Exception ClassifierException raised while decision tree predicting: Error: Column expected ([\'Width\', \'Height\', \'Sex\']) different than received ([\'Width\', \'Height\'])"\n'
    self.assertEqual(expected_exception, response.data)


  def test_throw_exception_if_action_columns_do_not_have_same_size_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 1, 1], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception ClassifierException raised while decision tree predicting: '\
            b'Error: Action column sizes are not equal; sizes found: [9, 10]"\n', response.data)


  def test_throw_exception_if_actual_values_missing_greater_than_number_of_values_on_predict(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_body_as_string = json.dumps(predict_body)

    # When
    response = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception ClassifierException raised while decision tree predicting: '\
            b'Error: Invalid prediction column size. Prediction: 7, Action: 9, Values to predict: 1"\n', response.data)


  def test_predict_accuracy_and_reset_state(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(100.0, float(response.data))


  def test_throw_exception_if_start_not_called_on_predict_accuracy(self) -> None:
    # Given
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assertEqual(b'"[123] Exception EngineComputationException raised while computing decision tree predict accuracy: ' \
            b'Error, No Data was set to launch decision tree computation."\n', response.data)


  def test_throw_exception_if_data_not_called_on_predict_accuracy(self) -> None:
    # Given
    start_body_as_string = build_start_body_as_string(123)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception EngineComputationException raised while computing decision tree predict accuracy: '\
            b'Error, No Data was set to launch decision tree computation."\n', response.data)


  def test_throw_exception_if_not_all_columns_received_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    expected_exception = b'"[123] Exception ClassifierException raised while computing decision tree predict accuracy: Error: Column expected ([\'Width\', \'Height\', \'Sex\']) different than received ([\'Sex\', \'Height\'])"\n'
    self.assertEqual(expected_exception, response.data)


  def test_throw_exception_if_action_columns_do_not_have_same_size_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception ClassifierException raised while computing decision tree predict accuracy: '\
            b'Error: Action column sizes are not equal; sizes found: [9, 5]"\n', response.data)


  def test_throw_exception_if_actual_values_missing_on_predict_accuracy(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    width_data_body = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string = json.dumps(width_data_body)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string, content_type=self.CONTENT_TYPE)
    height_data_body = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string = json.dumps(height_data_body)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string, content_type=self.CONTENT_TYPE)
    predict_accuracy_body = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string = json.dumps(predict_accuracy_body)

    # When
    response = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'"[123] Exception ClassifierException raised while computing decision tree predict accuracy: '\
            b'Error: Invalid prediction column size. Prediction: 8, Action: 9, Values to predict: 1"\n', response.data)


  def test_reset_state_on_cancel(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    cancel_body = dict(requestId=123, classifierType='DECISION_TREE')
    cancel_body_as_string = json.dumps(cancel_body)

    # When
    response = test_app.post(self.CANCEL_RESOURCE, data=cancel_body_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(b'{"Status":"Ok"}', response.data)


  def test_reset_state_on_cancel_and_start_new_request(self) -> None:
    # Given
    start_body = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string = json.dumps(start_body)
    test_app.post(self.START_RESOURCE, data=start_body_as_string, content_type=self.CONTENT_TYPE)
    sex_data_body = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string = json.dumps(sex_data_body)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string, content_type=self.CONTENT_TYPE)
    cancel_body = dict(requestId=123, classifierType='DECISION_TREE')
    cancel_body_as_string = json.dumps(cancel_body)

    # When
    response = test_app.post(self.CANCEL_RESOURCE, data=cancel_body_as_string, content_type=self.CONTENT_TYPE)
    start_body_2 = dict(requestId=123, predictionColumnName='Width', actionColumnNames=['Sex', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_2_as_string = json.dumps(start_body_2)
    test_app.post(self.START_RESOURCE, data=start_body_2_as_string, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_on_state(123, 'Width', ['Sex', 'Height'], 1, {})


  def test_multiple_requests_in_parallel(self) -> None:
    # Given
    # StartBody1
    start_body_1 = dict(requestId=123, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string_1 = json.dumps(start_body_1)
    test_app.post(self.START_RESOURCE, data=start_body_as_string_1, content_type=self.CONTENT_TYPE)
    # Data1
    sex_data_body_1 = dict(requestId=123, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1, 0], classifierType='DECISION_TREE')
    sex_data_body_as_string_1 = json.dumps(sex_data_body_1)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string_1, content_type=self.CONTENT_TYPE)
    # StartBody2
    start_body_2 = dict(requestId=999, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=1, classifierType='DECISION_TREE')
    start_body_as_string_2 = json.dumps(start_body_2)
    test_app.post(self.START_RESOURCE, data=start_body_as_string_2, content_type=self.CONTENT_TYPE)
    # Data1
    width_data_body_1 = dict(requestId=123, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string_1 = json.dumps(width_data_body_1)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string_1, content_type=self.CONTENT_TYPE)
    # Data2
    sex_data_body_2 = dict(requestId=999, columnName='Sex', values=[0, 1, 0, 1, 0, 1, 0, 1], classifierType='DECISION_TREE')
    sex_data_body_as_string_2 = json.dumps(sex_data_body_2)
    test_app.post(self.DATA_RESOURCE, data=sex_data_body_as_string_2, content_type=self.CONTENT_TYPE)
    # Data2
    width_data_body_2 = dict(requestId=999, columnName='Width', values=[0, 0, 1, 1, 0, 0, 1, 1, 0], classifierType='DECISION_TREE')
    width_data_body_as_string_2 = json.dumps(width_data_body_2)
    test_app.post(self.DATA_RESOURCE, data=width_data_body_as_string_2, content_type=self.CONTENT_TYPE)
    # Data1
    height_data_body_1 = dict(requestId=123, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string_1 = json.dumps(height_data_body_1)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string_1, content_type=self.CONTENT_TYPE)
    # Data2
    height_data_body_2 = dict(requestId=999, columnName='Height', values=[0, 0, 0, 0, 1, 1, 1, 1, 0], classifierType='DECISION_TREE')
    height_data_body_as_string_2 = json.dumps(height_data_body_2)
    test_app.post(self.DATA_RESOURCE, data=height_data_body_as_string_2, content_type=self.CONTENT_TYPE)

    # When1
    predict_accuracy_body_1 = dict(requestId=123, classifierType='DECISION_TREE')
    predict_accuracy_body_as_string_1 = json.dumps(predict_accuracy_body_1)
    response_1 = test_app.post(self.PREDICT_ACCURACY_RESOURCE, data=predict_accuracy_body_as_string_1, content_type=self.CONTENT_TYPE)
    # When2
    predict_body_2 = dict(requestId=999, classifierType='DECISION_TREE')
    predict_body_as_string_2 = json.dumps(predict_body_2)
    response_2 = test_app.post(self.PREDICT_RESOURCE, data=predict_body_as_string_2, content_type=self.CONTENT_TYPE)

    # Then
    self.assert_request_cancelled(123)
    self.assertEqual(100.0, float(response_1.data))
    self.assert_request_cancelled(999)
    self.assertEqual(b'{"requestId": 999, "columnName": "Sex", "values": [0], "classifierType": "DECISION_TREE"}', response_2.data)


def set_start_data(request_id: int, prediction_column_name: str, action_column_names: [str], number_of_values: int) -> None:
  classifier_data_builder = classifier_controller.classifier_data_builder_registry.new_builder(123)
  classifier_data_builder.set_start_data(prediction_column_name, action_column_names, number_of_values)


def add_data(request_id: int, column: str, values: [int]) -> None:
  classifier_controller.classifier_data_builder_registry.get_builder(request_id).add_data(column, values)


def build_start_body_as_string(request_id: int) -> str:
  body = dict(requestId=request_id, predictionColumnName='Sex', actionColumnNames=['Width', 'Height'], numberOfValues=5, classifierType='DECISION_TREE')
  return json.dumps(body)


def build_data_body_as_string(request_id: int) -> str:
  data_body = dict(requestId=request_id, columnName='Sex', values=[1, 0, 1, 0, 0], classifierType='DECISION_TREE')
  return json.dumps(data_body)
