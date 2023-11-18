#!/usr/bin/python3

import json
from flask import request
from logging import Logger
from engine_state import Engine, Request, RequestType
from utils.json_complex_encoder import JsonComplexEncoder
from utils.controller_utils import build_no_content_response
from exception.engine_computation_exception import EngineComputationException
from classifier.service.classifier_service_factory import ClassifierServiceFactory
from classifier.model.classifier_type import ClassifierType
from classifier.model.classifier_start_request import ClassifierStartRequest
from classifier.model.classifier_data_request import ClassifierDataRequest
from classifier.model.classifier_request import ClassifierRequest
from classifier.model.classifier_cancel_request import ClassifierCancelRequest
from classifier.model.classifier_response import ClassifierResponse


class ClassifierController:


  def __init__(self, engine: Engine, logger: Logger):
    self.engine = engine
    self.logger = logger


  def start(self) -> str:
    '''
    Signal that a computation will start

    Arguments
      classifier_start_request_json (str) - json corresponding to ClassifierStartRequest
                                            containing column names and number of values
    '''

    request_id = None
    try:
      classifier_start_request = ClassifierStartRequest.from_json(request.json)
      request_id = classifier_start_request.get_request_id()
      classifier_type = classifier_start_request.get_classifier_type()

      self.logger.info('[Start][%d] Start %s', request_id, classifier_type.to_lower_case_with_space())

      new_request = self.engine.register_new_request(request_id, RequestType.CLASSIFIER)

      return self._start(self.engine, new_request, classifier_start_request)

    except Exception as exception:
      self.engine.release_request(request_id)

      error_message = '[%s] Exception %s raised while starting %s: %s' % (request_id, type(exception).__name__, classifier_type.to_lower_case_with_space(), exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.logger.info('[End][%d] Start %s', request_id, classifier_type.to_lower_case_with_space())


  def on_data_received(self) -> str:
    '''
    Receive the data of the computation

    Arguments
      classifier_data_request_json (str) - json corresponding to ClassifierDataRequest
                                          containing column name with its values
    '''

    request_id = None
    try:
      classifier_data_request = ClassifierDataRequest.from_json(request.json)
      request_id = classifier_data_request.get_request_id()
      classifier_type = classifier_data_request.get_classifier_type()

      self.logger.info('[Start][%d] Receiving %s Data', request_id, classifier_type.to_lower_case_with_space())

      return self._on_data_received(self.engine, classifier_data_request)

    except Exception as exception:
      self.engine.release_request(request_id)

      error_message = '[%s] Exception %s raised while receiving %s data: %s' % (request_id, type(exception).__name__, classifier_type.to_lower_case_with_space(), exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.logger.info('[End][%d] Receiving %s Data', request_id, classifier_type.to_lower_case_with_space())


  def predict(self) -> str:
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
      classifier_request = ClassifierRequest.from_json(request.json)
      request_id = classifier_request.get_request_id()
      classifier_type = classifier_request.get_classifier_type()

      self.logger.info('[Start][%d] %s Predict', request_id, classifier_type.to_lower_case_with_space())

      return self._predict(self.engine, classifier_request)

    except Exception as exception:
      error_message = '[%s] Exception %s raised while %s predicting: %s' % (request_id, type(exception).__name__, classifier_type.to_lower_case_with_space(), exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.engine.release_request(request_id)

      self.logger.info('[End][%d] %s Predict', request_id, classifier_type.to_lower_case_with_space())


  def compute_accuracy_of_predict(self) -> str:
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
      classifier_request = ClassifierRequest.from_json(request.json)
      request_id = classifier_request.get_request_id()
      classifier_type = classifier_request.get_classifier_type()

      self.logger.info('[Start][%d] %s Predict accuracy', request_id, classifier_type.to_lower_case_with_space())

      return self._compute_accuracy_of_predict(self.engine, classifier_request)

    except Exception as exception:
      error_message = '[%s] Exception %s raised while computing %s predict accuracy: %s' % (request_id, type(exception).__name__, classifier_type.to_lower_case_with_space(), exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.engine.release_request(request_id)

      self.logger.info('[End][%d] %s Predict accuracy', request_id, classifier_type.to_lower_case_with_space())


  # TODO: Make this generic for all engine instead of only for Classifier Requests
  def cancel(self) -> str:
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
      classifier_type = classifier_cancel_request.get_classifier_type()

      self.logger.info('[Start][%d] Cancel %s Request', request_id, classifier_type.to_lower_case_with_space())

      self.engine.release_request(request_id)

      return build_no_content_response()

    except Exception as exception:
      self.engine.release_request(request_id)

      error_message = '[%s] Exception %s raised while cancelling %s: %s' % (request_id, type(exception).__name__, classifier_type.to_lower_case_with_space(), exception)
      self.logger.error(error_message)
      self.logger.exception(exception)
      raise EngineComputationException(error_message)

    finally:
      self.logger.info('[End][%d] Cancel %s Request', request_id, classifier_type.to_lower_case_with_space())


  @classmethod
  def _start(cls, engine: Engine, request: Request, classifier_start_request: ClassifierStartRequest) -> str:
    request_id = classifier_start_request.get_request_id()

    cls.__throw_exception_if_data_available_on_start(engine, request_id)

    prediction_column_name = classifier_start_request.get_prediction_column_name()
    action_column_names = classifier_start_request.get_action_column_names()
    number_of_values = classifier_start_request.get_number_of_values()

    request.set_classifier_start_data(
      prediction_column_name,
      action_column_names,
      number_of_values
    )

    return build_no_content_response()


  @classmethod
  def _on_data_received(cls, engine: Engine, classifier_data_request: ClassifierDataRequest) -> str:
    request_id = classifier_data_request.get_request_id()

    cls.__throw_exception_if_start_was_not_called(engine, request_id)

    column_name = classifier_data_request.get_column_name()
    values = classifier_data_request.get_values()
    engine.get_request(request_id).add_classifier_data(column_name, values)

    return build_no_content_response()


  @classmethod
  def _predict(cls, engine: Engine, classifier_request: ClassifierRequest) -> str:
    request_id = classifier_request.get_request_id()
    classifier_type = classifier_request.get_classifier_type()

    cls.__throw_exception_if_data_is_none_on_computation(engine, request_id, classifier_type)

    classifier_data = engine.get_request(request_id).build_classifier_data()

    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()
    classifier_service = ClassifierServiceFactory.build_service(classifier_type, data, action_column_names, prediction_column_name, number_of_values)

    predicted_data_frame = classifier_service.predict()

    classifier_response = ClassifierResponse.from_data_frame(predicted_data_frame, request_id, prediction_column_name, classifier_type)

    return json.dumps(classifier_response, cls=JsonComplexEncoder)


  @classmethod
  def _compute_accuracy_of_predict(cls, engine: Engine, classifier_request: ClassifierRequest) -> str:
    request_id = classifier_request.get_request_id()
    classifier_type = classifier_request.get_classifier_type()

    cls.__throw_exception_if_data_is_none_on_computation(engine, request_id, classifier_type)

    classifier_data = engine.get_request(request_id).build_classifier_data()

    data = classifier_data.to_data_frame()
    action_column_names = classifier_data.get_action_column_names()
    prediction_column_name = classifier_data.get_prediction_column_name()
    number_of_values = classifier_data.get_number_of_values()
    classifier_service = ClassifierServiceFactory.build_service(classifier_type, data, action_column_names, prediction_column_name, number_of_values)

    return str(classifier_service.compute_predict_accuracy())


  @classmethod
  def __throw_exception_if_data_is_none_on_computation(cls, engine: Engine, request_id: int, classifier_type: ClassifierType) -> None:
    if not engine.contains_request(request_id) or\
      not engine.get_request(request_id).contains_classifier_start_data() or\
      not engine.get_request(request_id).contains_clasifier_data():
      error_message = 'Error, No Data was set to launch %s computation.' % (classifier_type.to_lower_case_with_space())
      raise EngineComputationException(error_message)


  @classmethod
  def __throw_exception_if_data_available_on_start(cls, engine: Engine, request_id: int) -> None:
    if engine.contains_request(request_id) and\
      engine.get_request(request_id).contains_clasifier_data():
      error_message = 'Error, Launching start with existing inflight request with id: %s.' % (request_id)
      raise EngineComputationException(error_message)


  @classmethod
  def __throw_exception_if_start_was_not_called(cls, engine: Engine, request_id: int) -> None:
    if not engine.contains_request(request_id) or\
      not engine.get_request(request_id).contains_classifier_start_data():
      error_message = 'Error, Receiving Data without Start.'
      raise EngineComputationException(error_message)
