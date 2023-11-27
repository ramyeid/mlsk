#!/usr/bin/python3

from typing import Dict
from enum import Enum
from datetime import datetime
from multiprocessing import Pipe
from multiprocessing.connection import Connection
from classifier.registry.classifier_data_builder import ClassifierDataBuilder, ClassifierData


class RequestRegistryException(Exception):
  '''
  Exception that will be thrown by Engine when registering or retrieving a request.
  '''


class RequestType(Enum):
  '''
  Represents the Type of requests received from service
  '''

  CLASSIFIER = 'CLASSIFIER'
  TIME_SERIES_ANALYSIS = 'TIME_SERIES_ANALYSIS'


class ReleaseRequestType(Enum):
  '''
  Represents the messages sent on the Pipe of the request release
  '''

  IGNORE = 'IGNORE'   # Simply ignore this message and move on, used to unblock the thread launched to check the pipe
  RELEASE = 'RELEASE' # Real release message received


class Request:
  '''
  Represents the inflight request state, currently being handled by the engine
  This class also holds the logic to release the request with a rx and tx channel.

  Attributes
    request_id              (int)                     - unique Id of an inflight request
    request_type            (RequestType)             - type of the inflight request
    creation_datetime       (datetime)                - Creation datetime of request
    release_request_rx      (Connection)              - receiver of a channel where a release request will be posted
    release_request_tx      (Connection)              - transmitter of a channel where a release request will be posted
    classifier_data_builder (ClassifierDataBuilder)   - holds the state of the inflight classifier request
  '''

  def __init__(self, request_id: int, request_type: RequestType):
    self.request_id = request_id
    self.request_type = request_type
    self.creation_datetime = datetime.now()
    self.release_request_rx, self.release_request_tx = Pipe()
    self.classifier_data_builder = ClassifierDataBuilder()


  def get_request_id(self) -> int:
    return self.request_id


  def get_request_type(self) -> RequestType:
    return self.request_type


  def get_creation_datetime(self) -> datetime:
    return self.creation_datetime


  def get_release_request_rx(self) -> Connection:
    return self.release_request_rx


  def post_release_request(self, release_type: ReleaseRequestType) -> None:
    return self.release_request_tx.send(release_type)


  def set_classifier_start_data(self, prediction_column_names: str, action_column_names: [str], number_of_values: int) -> None:
    if self.request_type == RequestType.CLASSIFIER:
      self.classifier_data_builder.set_start_data(prediction_column_names, action_column_names, number_of_values)
    else:
      raise RequestRegistryException('Unable to set classifier start data on `%s` request' % self.request_type.value)


  def add_classifier_data(self, column_name: str, values: [int]) -> None:
    if self.request_type == RequestType.CLASSIFIER:
      self.classifier_data_builder.add_data(column_name, values)
    else:
      raise RequestRegistryException('Unable to add classifier data on `%s` request' % self.request_type.value)


  def build_classifier_data(self) -> ClassifierData:
    if self.request_type == RequestType.CLASSIFIER:
      return self.classifier_data_builder.build_classifier_data()
    else:
      raise RequestRegistryException('Unable to build classifier data on `%s` request' % self.request_type.value)


  def contains_classifier_start_data(self) -> bool:
    if self.request_type == RequestType.CLASSIFIER:
      return self.classifier_data_builder.contains_start_data()
    else:
      return False


  def contains_clasifier_data(self) -> bool:
    if self.request_type == RequestType.CLASSIFIER:
      return self.classifier_data_builder.contains_data()
    else:
      return False


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def to_json(self) -> dict:
    return dict(requestId=self.request_id,\
      requestType=self.request_type,\
      classifierDataBuilder=str(self.classifier_data_builder),\
      creationDateime=self.creation_datetime
    )


class Engine:
  '''
  Represents the state of the engine.
  For example this instance will contain all the inflight requests, the classifier data currently being built.

  Attributes
    inflight_requests ({requestId (int) -> Request]) - map requestId to a Request
  '''


  def __init__(self):
    self.inflight_requests = {}


  def get_inflight_requests(self) -> Dict[int, Request]:
    return self.inflight_requests


  def register_new_request(self, request: Request) -> None:
    request_id = request.get_request_id()

    if request_id in self.inflight_requests:
      raise RequestRegistryException('RequestId ({}) already inflight!'.format(request_id))

    self.inflight_requests[request_id] = request


  def get_request(self, request_id: int) -> Request:
    if request_id not in self.inflight_requests:
      raise RequestRegistryException('RequestId ({}) not inflight!'.format(request_id))
    return self.inflight_requests[request_id]


  def contains_request(self, request_id: int) -> bool:
    return request_id in self.inflight_requests


  def release_request(self, request_id: int) -> bool:
    if request_id in self.inflight_requests:
      request_to_release = self.inflight_requests.pop(request_id)
      request_to_release.post_release_request(ReleaseRequestType.RELEASE)
      return True
    else:
      return False


  def release_all_inflight_requests(self) -> None:
    inflight_request_ids = set(self.inflight_requests.keys())
    for request_id in inflight_request_ids:
      self.release_request(request_id)


  def request_count(self) -> None:
    return len(self.inflight_requests)
