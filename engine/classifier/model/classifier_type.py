#!/usr/bin/python3

from enum import Enum


class ClassifierType(Enum):
  '''
  Represents the ClassifierType that will be propagated from the service
  ClassifierType is an enum representing the type of classifier to launch
  '''

  DECISION_TREE = 'DECISION_TREE'


  def to_lower_case_with_space(self) -> str:
    return self.value.lower().replace('_', ' ')


  def __str__(self) -> str:
    return str(self.value)


  def __repr__(self) -> str:
    return self.__str__()
