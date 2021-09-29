#!/usr/bin/python3

import json
from flask import request
from utils.json_complex_encoder import JsonComplexEncoder
from utils.logger import get_logger
from exception.engine_computation_exception import EngineComputationException
from classifier.service.decision_tree_service import DecisionTreeService
from classifier.model.classifier_start_request import ClassifierStartRequest
from classifier.model.classifier_data_request import ClassifierDataRequest
from classifier.model.classifier_data_response import ClassifierDataResponse
from classifier.model.classifier_data import ClassifierDataBuilder


classifier_data_builder = ClassifierDataBuilder()


def start() -> str:
  '''
  Signal that a computation will start

  Arguments
    classifier_start_request_json (str) - json corresponding to ClassifierStartRequest
                                          containing column names and number of values
  '''

  try:
    global classifier_data_builder

    get_logger().info('[Start] Start Decision Tree')
    __throw_exception_if_data_available_on_start()

    classifier_start_request = ClassifierStartRequest.from_json(request.json)

    prediction_column_name = classifier_start_request.get_prediction_column_name()
    action_column_names = classifier_start_request.get_action_column_names()
    number_of_values = classifier_start_request.get_number_of_values()
    classifier_data_builder.set_start_data(prediction_column_name, action_column_names, number_of_values)

    return 'Ok'
  except Exception as exception:
    __reset_state()
    error_message = 'Exception %s raised while starting decision tree: %s' % (type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_logger().info('[End] Start Decision Tree')


def on_data_received() -> str:
  '''
  Receive the data of the computation

  Arguments
    classifier_data_request_json (str) - json corresponding to ClassifierDataRequest
                                         containing column name with its values
  '''

  try:
    global classifier_data_builder

    get_logger().info('[Start] Receiving Decision Tree Data')
    __throw_exception_if_start_was_not_called()

    classifier_data_request = ClassifierDataRequest.from_json(request.json)

    column_name = classifier_data_request.get_column_name()
    values = classifier_data_request.get_values()
    classifier_data_builder.add_data(column_name, values)

    return 'Ok'
  except Exception as exception:
    __reset_state()
    error_message = 'Exception %s raised while receiving decision tree data: %s' % (type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_logger().info('[End] Receiving Decision Tree Data')


def predict() -> str:
  '''
  Predict values.

  Returns
    classifier_data_response -> classifier_data_response corresponding to the predicted values
  '''

  try:
    global classifier_data_builder

    get_logger().info('[Start] Decision Tree Predict')
    __throw_exception_if_data_is_none_on_computation()

    classifier_data = classifier_data_builder.build_classifier_data()
    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()

    decision_tree_service = DecisionTreeService(data, action_column_names, prediction_column_name, number_of_values)
    predicted_data_frame = decision_tree_service.predict()

    predicted_classifier_data = ClassifierDataResponse.from_data_frame(predicted_data_frame, prediction_column_name)
    return json.dumps(predicted_classifier_data, cls=JsonComplexEncoder)

  except Exception as exception:
    error_message = 'Exception %s raised while predicting: %s' % (type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    __reset_state()
    get_logger().info('[End] Decision Tree Predict')


def compute_accuracy_of_predict() -> str:
  '''
  Compute accuracy of predict.
  Compute prediction on {number_of_values} and compare with actual.

  Returns
    float -> accuracy of the predict algorithm percentage
  '''

  try:
    global classifier_data_builder

    get_logger().info('[Start] Decision Tree Predict accuracy')
    __throw_exception_if_data_is_none_on_computation()

    classifier_data = classifier_data_builder.build_classifier_data()
    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()

    decision_tree_service = DecisionTreeService(data, action_column_names, prediction_column_name, number_of_values)

    return str(decision_tree_service.compute_predict_accuracy())

  except Exception as exception:
    error_message = 'Exception %s raised while computing predict accuracy: %s' % (type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    __reset_state()
    get_logger().info('[End] Decision Tree Predict accuracy')


def __throw_exception_if_data_is_none_on_computation() -> None:
  global classifier_data_builder

  if ((not classifier_data_builder.contains_start_data()) or (not classifier_data_builder.contains_data())):
    error_message = 'Error, No Data was set to launch Decision Tree computation.'
    raise EngineComputationException(error_message)


def __throw_exception_if_data_available_on_start() -> None:
  global classifier_data_builder

  if (classifier_data_builder.contains_start_data() or classifier_data_builder.contains_data()):
    error_message = 'Error, Launching start with existing State, Resetting State.'
    raise EngineComputationException(error_message)


def __throw_exception_if_start_was_not_called() -> None:
  global classifier_data_builder

  if (not classifier_data_builder.contains_start_data()):
    error_message = 'Error, Receiving Data without Start, Resetting State.'
    raise EngineComputationException(error_message)


def __reset_state() -> None:
  global classifier_data_builder

  classifier_data_builder.reset()
