#!/usr/bin/python3

from __future__ import annotations
import pandas as pd
from classifier.model.classifier_type import ClassifierType
from classifier.service.decision_tree_service import DecisionTreeService
from classifier.service.classifier_service import IClassifierService


class ClassifierServiceFactory:
  '''
  Builds the classifier service from the classifier type
  '''

  @classmethod
  def build_service(cls, classifier_type: ClassifierType,
                    data: pd.DataFrame, action_column_names: [str],
                    prediction_column_name: str, number_of_values: int) -> IClassifierService:
    if classifier_type == ClassifierType.DECISION_TREE:
      return DecisionTreeService(data, action_column_names, prediction_column_name, number_of_values)
    else:
      raise Exception('Unhandled classifier_type %s' % classifier_type)
