#!/usr/bin/python3

from __future__ import annotations
from classifier.model.classifier_type import ClassifierType


class ClassifierCancelRequest:
  '''
  Represents the request that will be sent to the classifier controller
  to cancel a specific request

  Attributes
    request_id (int)                 - unique id assigned to a request
    classifier_type (ClassifierType) - classifier algorithm to use
  '''

  def __init__(self, request_id: int, classifier_type: ClassifierType):
    self.request_id = request_id
    self.classifier_type = classifier_type


  def get_request_id(self) -> int:
    return self.request_id


  def get_classifier_type(self) -> ClassifierType:
    return self.classifier_type


  def __str__(self) -> str:
    return str(self.to_json())


  def __repr__(self) -> str:
    return self.__str__()


  def __eq__(self, other) -> bool:
    return isinstance(other, ClassifierCancelRequest) and\
      self.request_id == other.request_id and\
      self.classifier_type == other.classifier_type


  def to_json(self) -> dict:
    return dict(requestId=self.request_id, classifierType=str(self.classifier_type))


  @classmethod
  def from_json(cls, data: dict) -> ClassifierCancelRequest:
    request_id = int(data['requestId'])
    classifier_type = ClassifierType[data['classifierType']]

    return cls(request_id, classifier_type)
