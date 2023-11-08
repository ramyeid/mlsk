#!/usr/bin/python3

import unittest
from classifier.model.classifier_type import ClassifierType


class TestClassifierType(unittest.TestCase):


  def test_decision_tree_Type(self) -> None:
    # Given
    classifier_type = ClassifierType.DECISION_TREE

    # When
    actual_str = str(classifier_type)
    actual_lower_with_space = classifier_type.to_lower_case_with_space()

    # Then
    expected_str = 'DECISION_TREE'
    expected_lower_with_space = 'decision tree'
    self.assertEqual(expected_str, actual_str)
    self.assertEqual(expected_lower_with_space, actual_lower_with_space)


if __name__ == '__main__':
  unittest.main()
