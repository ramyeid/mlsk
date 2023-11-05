#!/usr/bin/python3

import json
from flask import request
from utils.json_complex_encoder import JsonComplexEncoder
from utils.logger import get_logger
from utils.controller_utils import build_default_response
from exception.engine_computation_exception import EngineComputationException
from classifier.service.decision_tree_service import DecisionTreeService
from classifier.model.classifier_start_request import ClassifierStartRequest
from classifier.model.classifier_data_request import ClassifierDataRequest
from classifier.model.classifier_request import ClassifierRequest
from classifier.model.classifier_cancel_request import ClassifierCancelRequest
from classifier.model.classifier_response import ClassifierResponse
from classifier.model.classifier_data import ClassifierDataBuilder


classifier_data_builder = ClassifierDataBuilder()


def start() -> str:
  '''
  Signal that a computation will start

  Arguments
    classifier_start_request_json (str) - json corresponding to ClassifierStartRequest
                                          containing column names and number of values
  '''

  request_id = None
  try:
    global classifier_data_builder

    classifier_start_request = ClassifierStartRequest.from_json(request.json)
    request_id = classifier_start_request.get_request_id()

    get_logger().info('[Start][%d] Start Decision Tree', request_id)

    __throw_exception_if_data_available_on_start()

    prediction_column_name = classifier_start_request.get_prediction_column_name()
    action_column_names = classifier_start_request.get_action_column_names()
    number_of_values = classifier_start_request.get_number_of_values()

    classifier_data_builder.set_start_data(prediction_column_name, action_column_names, number_of_values)

    return build_default_response()

  except Exception as exception:
    __cancel_request()
    error_message = '[%s] Exception %s raised while starting decision tree: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_logger().info('[End][%d] Start Decision Tree', request_id)


def on_data_received() -> str:
  '''
  Receive the data of the computation

  Arguments
    classifier_data_request_json (str) - json corresponding to ClassifierDataRequest
                                         containing column name with its values
  '''

  request_id = None
  try:
    global classifier_data_builder

    classifier_data_request = ClassifierDataRequest.from_json(request.json)
    request_id = classifier_data_request.get_request_id()

    get_logger().info('[Start][%d] Receiving Decision Tree Data', request_id)

    __throw_exception_if_start_was_not_called()

    column_name = classifier_data_request.get_column_name()
    values = classifier_data_request.get_values()
    classifier_data_builder.add_data(column_name, values)

    return build_default_response()

  except Exception as exception:
    __cancel_request()
    error_message = '[%s] Exception %s raised while receiving decision tree data: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_logger().info('[End][%d] Receiving Decision Tree Data', request_id)


def predict() -> str:
  '''
  Predict values.

  Arguments
    request_json (str) - json corresponding to ClassifierRequest
                         containing request id

  Returns
    classifier_response -> classifier_response corresponding to the predicted values
  '''

  request_id = None
  try:
    global classifier_data_builder

    classifier_request = ClassifierRequest.from_json(request.json)
    request_id = classifier_request.get_request_id()

    get_logger().info('[Start][%d] Decision Tree Predict', request_id)

    __throw_exception_if_data_is_none_on_computation()

    classifier_data = classifier_data_builder.build_classifier_data()

    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()

    decision_tree_service = DecisionTreeService(data, action_column_names, prediction_column_name, number_of_values)
    predicted_data_frame = decision_tree_service.predict()

    classifier_response = ClassifierResponse.from_data_frame(predicted_data_frame, request_id, prediction_column_name)

    return json.dumps(classifier_response, cls=JsonComplexEncoder)

  except Exception as exception:
    error_message = '[%s] Exception %s raised while predicting: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    __cancel_request()
    get_logger().info('[End][%d] Decision Tree Predict', request_id)


def compute_accuracy_of_predict() -> str:
  '''
  Compute accuracy of predict.
  Compute prediction on {number_of_values} and compare with actual.

  Arguments
    request_json (str) - json corresponding to ClassifierRequest
                         containing request id

  Returns
    float -> accuracy of the predict algorithm percentage
  '''

  request_id = None
  try:
    global classifier_data_builder

    classifier_request = ClassifierRequest.from_json(request.json)
    request_id = classifier_request.get_request_id()

    get_logger().info('[Start][%d] Decision Tree Predict accuracy', request_id)

    __throw_exception_if_data_is_none_on_computation()

    classifier_data = classifier_data_builder.build_classifier_data()

    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()

    decision_tree_service = DecisionTreeService(data, action_column_names, prediction_column_name, number_of_values)

    return str(decision_tree_service.compute_predict_accuracy())

  except Exception as exception:
    error_message = '[%s] Exception %s raised while computing predict accuracy: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    __cancel_request()
    get_logger().info('[End][%d] Decision Tree Predict accuracy', request_id)


# TODO: Make this generic for all engine instead of only for Classifier Requests
def cancel() -> str:
  '''
  Cancel request and resets state

  Arguments
    classifier_cancel_request_json (str) - json corresponding to ClassifierCancelRequest
                                           containing request id
  '''

  request_id = None
  try:
    classifier_cancel_request = ClassifierCancelRequest.from_json(request.json)
    request_id = classifier_cancel_request.get_request_id()

    get_logger().info('[Start][%d] Cancel Decision Tree Request', request_id)
    __cancel_request()

    return build_default_response()

  except Exception as exception:
    __cancel_request()
    error_message = '[%s] Exception %s raised while cancelling decision tree: %s' % (request_id, type(exception).__name__, exception)
    get_logger().error(error_message)
    get_logger().exception(exception)
    raise EngineComputationException(error_message)

  finally:
    get_logger().info('[End][%d] Cancel Decision Tree Request', request_id)


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


def __cancel_request() -> None:
  global classifier_data_builder

  classifier_data_builder.reset()
